/*
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gs.dmn.transformation.basic;

import com.gs.dmn.DMNModelRepository;
import com.gs.dmn.DRGElementReference;
import com.gs.dmn.ImportPath;
import com.gs.dmn.QualifiedName;
import com.gs.dmn.ast.*;
import com.gs.dmn.context.DMNContext;
import com.gs.dmn.context.environment.Declaration;
import com.gs.dmn.context.environment.Environment;
import com.gs.dmn.context.environment.EnvironmentFactory;
import com.gs.dmn.context.environment.VariableDeclaration;
import com.gs.dmn.el.analysis.semantics.type.AnyType;
import com.gs.dmn.el.analysis.semantics.type.Type;
import com.gs.dmn.el.analysis.syntax.ast.expression.Expression;
import com.gs.dmn.el.synthesis.ELTranslator;
import com.gs.dmn.feel.analysis.semantics.type.*;
import com.gs.dmn.feel.analysis.syntax.ast.expression.context.Context;
import com.gs.dmn.feel.analysis.syntax.ast.expression.context.ContextEntry;
import com.gs.dmn.feel.analysis.syntax.ast.expression.function.FormalParameter;
import com.gs.dmn.feel.analysis.syntax.ast.expression.literal.StringLiteral;
import com.gs.dmn.feel.lib.StringEscapeUtil;
import com.gs.dmn.runtime.DMNRuntimeException;
import com.gs.dmn.runtime.Pair;
import com.gs.dmn.serialization.DMNVersion;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StandardDMNEnvironmentFactory implements DMNEnvironmentFactory {
    protected final BasicDMNToNativeTransformer<Type, DMNContext> dmnTransformer;

    protected final DMNModelRepository dmnModelRepository;
    protected final EnvironmentFactory environmentFactory;

    protected final ELTranslator<Type, DMNContext> feelTranslator;

    private final FEELTypeMemoizer feelTypeMemoizer;
    private final EnvironmentMemoizer environmentMemoizer;

    public StandardDMNEnvironmentFactory(BasicDMNToNativeTransformer<Type, DMNContext> dmnTransformer) {
        this.dmnTransformer = dmnTransformer;

        this.dmnModelRepository = dmnTransformer.getDMNModelRepository();
        this.environmentFactory = dmnTransformer.getEnvironmentFactory();

        this.feelTranslator = dmnTransformer.getFEELTranslator();

        this.environmentMemoizer = new EnvironmentMemoizer();
        this.feelTypeMemoizer = new FEELTypeMemoizer();
    }

    //
    // DRG Elements
    //
    @Override
    public Type drgElementOutputFEELType(TDRGElement element) {
        return drgElementOutputFEELType(element, this.dmnTransformer.makeGlobalContext(element));
    }

    @Override
    public Type drgElementOutputFEELType(TDRGElement element, DMNContext context) {
        Type type = this.drgElementVariableFEELType(element, context);
        return drgElementOutputFEELType(element, type);
    }

    private Type drgElementOutputFEELType(TDRGElement element, Type type) {
        if (element instanceof TInputData) {
            return type;
        } else if (element instanceof TDecision) {
            return type;
        } else if (element instanceof TInvocable) {
            if (type instanceof FunctionType) {
                return ((FunctionType) type).getReturnType();
            } else {
                throw new DMNRuntimeException(String.format("Expected function type for element '%s'. Found '%s'", element.getName(), type));
            }
        } else {
            throw new DMNRuntimeException(String.format("'%s' is not supported yet", element.getClass().getName()));
        }
    }

    @Override
    public Type drgElementVariableFEELType(TDRGElement element) {
        return drgElementVariableFEELType(element, this.dmnTransformer.makeGlobalContext(element));
    }

    @Override
    public Type drgElementVariableFEELType(TDRGElement element, DMNContext context) {
        TDefinitions model = this.dmnModelRepository.getModel(element);
        QualifiedName typeRef = this.dmnModelRepository.variableTypeRef(model, element);
        Type declaredType = this.dmnModelRepository.isNullOrAny(typeRef) ? null : toFEELType(model, typeRef);
        if (declaredType == null) {
            // Infer type from body
            return inferDRGElementVariableFEELType(element, context);
        } else if (!declaredType.isFullySpecified()) {
            Type inferredType = inferDRGElementVariableFEELType(element, context);
            return refineDeclaredType(declaredType, inferredType);
        } else {
            // Augment function type
            if (DMNFunctionType.class.equals(declaredType.getClass())) {
                if (element instanceof TInvocable) {
                    declaredType = ((DMNFunctionType) declaredType).attachElement(element);
                }
            }
            return declaredType;
        }
    }

    private Type refineDeclaredType(Type declaredType, Type inferredType) {
        if (declaredType == null) {
            return inferredType;
        }
        if (declaredType instanceof ItemDefinitionType && inferredType instanceof CompositeDataType) {
            ItemDefinitionType oldType = (ItemDefinitionType) declaredType;
            ItemDefinitionType newType = new ItemDefinitionType(oldType.getName(), oldType.getModelName());
            for (String member: oldType.getMembers()) {
                Type oldMemberType = oldType.getMemberType(member);
                if (oldMemberType == null || oldMemberType == AnyType.ANY) {
                    Type newMemberType = ((CompositeDataType) inferredType).getMemberType(member);
                    if (newMemberType != null && newMemberType != AnyType.ANY) {
                        newType.addMember(member, oldType.getAliases(member), newMemberType);
                    } else {
                        newType.addMember(member, oldType.getAliases(member), oldMemberType);
                    }
                }
            }
            return newType;
        }
        return declaredType;
    }

    private Type inferDRGElementVariableFEELType(TDRGElement element, DMNContext context) {
        if (element == null) {
            throw new DMNRuntimeException(String.format("Cannot infer type for DRG element '%s'", element));
        } else if (element instanceof TInputData) {
            if (dmnTransformer.isStrongTyping()) {
                throw new DMNRuntimeException(String.format("Cannot infer type for '%s.%s'", element.getClass().getSimpleName(), element.getName()));
            } else {
                return null;
            }
        } else if (element instanceof TDecision) {
            return expressionType(element, ((TDecision) element).getExpression(), context);
        } else if (element instanceof TBusinessKnowledgeModel) {
            return expressionType(element, ((TBusinessKnowledgeModel) element).getEncapsulatedLogic(), context);
        } else if (element instanceof TDecisionService) {
            return makeDSVariableType((TDecisionService) element);
        } else {
            throw new DMNRuntimeException(String.format("Type inference for '%s.%s' not supported yet", element.getClass().getSimpleName(), element.getName()));
        }
    }

    @Override
    public Type toFEELType(TInputData inputData) {
        TDefinitions model = this.dmnModelRepository.getModel(inputData);
        String typeRefString = QualifiedName.toName(inputData.getVariable().getTypeRef());
        QualifiedName typeRef = QualifiedName.toQualifiedName(model, typeRefString);
        return toFEELType(model, typeRef);
    }

    //
    // Expression related functions
    //
    @Override
    public Type expressionType(TDRGElement element, TExpression expression, DMNContext context) {
        if (expression == null) {
            return null;
        }

        TDefinitions model = this.dmnModelRepository.getModel(element);
        QualifiedName typeRef = QualifiedName.toQualifiedName(model, expression.getTypeRef());
        if (expression instanceof TContext) {
            if (!dmnModelRepository.isNullOrAny(typeRef)) {
                return toFEELType(model, typeRef);
            }

            //
            // Infer type from expression
            //
            // Collect members & return type
            List<TContextEntry> contextEntryList = ((TContext) expression).getContextEntry();
            List<Pair<String, Type>> members = new ArrayList<>();
            Type returnType = null;
            DMNContext localContext = this.dmnTransformer.makeLocalContext(element, (TContext) expression, context);
            for(TContextEntry entry: contextEntryList) {
                TInformationItem variable = entry.getVariable();
                if (variable != null) {
                    String name = variable.getName();
                    Type entryType = this.entryType(element, entry, localContext);
                    localContext.addDeclaration(this.environmentFactory.makeVariableDeclaration(name, entryType));
                    members.add(new Pair<>(name, entryType));
                } else {
                    returnType = this.entryType(element, entry, localContext);
                }
            }
            // Infer output type
            if (returnType == null) {
                ContextType contextType = new ContextType();
                for (Pair<String, Type> member: members) {
                    contextType.addMember(member.getLeft(), new ArrayList<>(), member.getRight());
                }
                return contextType;
            } else {
                return returnType;
            }
        } else if (expression instanceof TFunctionDefinition) {
            // Infer type from expression
            return functionDefinitionType(element, (TFunctionDefinition) expression, context);
        } else if (expression instanceof TLiteralExpression) {
            if (!this.dmnModelRepository.isNullOrAny(typeRef)) {
                return toFEELType(model, typeRef);
            }

            // Infer type from expression
            return literalExpressionType(element, (TLiteralExpression) expression, context);
        } else if (expression instanceof TInvocation) {
            if (!this.dmnModelRepository.isNullOrAny(typeRef)) {
                return toFEELType(model, typeRef);
            }

            // Infer type from expression
            TExpression body = ((TInvocation) expression).getExpression();
            if (body instanceof TLiteralExpression) {
                String bkmName = ((TLiteralExpression) body).getText();
                TBusinessKnowledgeModel bkm = this.dmnModelRepository.findKnowledgeModelByName(bkmName);
                if (bkm == null) {
                    throw new DMNRuntimeException(String.format("Cannot find BKM for '%s'", bkmName));
                }
                return drgElementOutputFEELType(bkm);
            } else {
                throw new DMNRuntimeException(String.format("Not supported '%s'", body.getClass().getSimpleName()));
            }
        } else if (expression instanceof TDecisionTable) {
            if (!this.dmnModelRepository.isNullOrAny(typeRef)) {
                return toFEELType(model, typeRef);
            }

            // Derive from outputClauses clauses and rules
            TDecisionTable dt = (TDecisionTable) expression;
            List<TOutputClause> outputClauses = dt.getOutput();
            List<TLiteralExpression> outputEntries = outputEntries(dt);
            Map<String, Type> members = new LinkedHashMap<>();
            if (outputClauses != null) {
                for (int i=0; i<outputClauses.size(); i++) {
                    // Derive typeRef from output clause
                    TOutputClause outputClause = outputClauses.get(i);
                    Type type = toFEELType(model, element, outputEntries, outputClause, i, context);
                    members.put(outputClause.getName(), type);
                }
                Type expressionType;
                if (members.isEmpty()) {
                    throw new DMNRuntimeException(String.format("Cannot infer type for '%s' from empty OutputClauses", element.getName()));
                } else if (members.size() == 1) {
                    expressionType = members.values().iterator().next();
                } else {
                    expressionType = new ContextType(members);
                }
                return applyPolicies(element, dt, expressionType);
            }
            throw new DMNRuntimeException(String.format("Cannot infer type for '%s' from empty OutputClauses", element.getName()));
        } else if (expression instanceof TList) {
            if (!this.dmnModelRepository.isNullOrAny(typeRef)) {
                Type elementType = toFEELType(model, typeRef);
                return new ListType(elementType);
            }

            List<? extends TExpression> listExpression = ((TList) expression).getExpression();
            Type elementType = AnyType.ANY;
            if (listExpression != null && !listExpression.isEmpty()) {
                elementType = expressionType(element, listExpression.get(0), context);
            }
            return new ListType(elementType);
        } else {
            throw new DMNRuntimeException(String.format("'%s' is not supported yet", expression.getClass().getSimpleName()));
        }
    }

    private List<TLiteralExpression> outputEntries(TDecisionTable dt) {
        List<TLiteralExpression> outputEntries = new ArrayList<>();
        List<TDecisionRule> rules = dt.getRule();
        if (rules != null && !rules.isEmpty()) {
            outputEntries = rules.get(0).getOutputEntry();
        }
        return outputEntries;
    }

    @Override
    public Type toFEELType(TDRGElement element, TOutputClause outputClause, int index) {
        TExpression expression = this.dmnModelRepository.expression(element);
        if (!(expression instanceof TDecisionTable)) {
            throw new DMNRuntimeException(String.format("Expected Decision Table in element '%s', found '%s'", element.getName(), expression == null ? null : expression.getClass().getName()));
        }

        TDefinitions model = this.dmnModelRepository.getModel(element);
        TDecisionTable dt = (TDecisionTable) expression;
        List<TLiteralExpression> outputEntries = this.outputEntries(dt);
        DMNContext context = this.dmnTransformer.makeGlobalContext(element);
        return this.toFEELType(model, element, outputEntries, outputClause, index, context);
    }

    private Type toFEELType(TDefinitions model, TDRGElement element, List<TLiteralExpression> outputEntries, TOutputClause outputClause, int index, DMNContext context) {
        String outputTypeRef = QualifiedName.toName(outputClause.getTypeRef());
        Type type;
        if (outputTypeRef == null) {
            if (index < outputEntries.size()) {
                type = expressionType(element, outputEntries.get(index), context);
                if (com.gs.dmn.el.analysis.semantics.type.Type.isNull(type)) {
                    throw new DMNRuntimeException(String.format("Cannot infer type for '%s' from OutputEntries", element.getName()));
                }
            } else {
                throw new DMNRuntimeException(String.format("Cannot infer type for '%s' from OutputEntries", element.getName()));
            }
        } else {
            type = toFEELType(model, outputTypeRef);
        }
        return type;
    }

    private Type applyPolicies(TDRGElement element, TDecisionTable decisionTable, Type type) {
        TBuiltinAggregator aggregation = decisionTable.getAggregation();
        if (decisionTable.getHitPolicy() == THitPolicy.COLLECT && !com.gs.dmn.el.analysis.semantics.type.Type.isNull(type)) {
            type = new ListType(type);
        }
        if (aggregation == TBuiltinAggregator.COUNT || aggregation == TBuiltinAggregator.SUM) {
            return NumberType.NUMBER;
        } else if (aggregation == TBuiltinAggregator.MIN || aggregation == TBuiltinAggregator.MAX) {
            if (type instanceof ListType) {
                return ((ListType) type).getElementType();
            } else {
                throw new DMNRuntimeException(String.format("Expected list type, found '%s' for element '%s", type, element.getName()));
            }
        } else {
            return type;
        }
    }

    @Override
    public Type convertType(Type type, boolean convertToContext) {
        if (convertToContext) {
            if (type instanceof ItemDefinitionType) {
                type = ((ItemDefinitionType) type).toContextType();
            }
        }
        return type;
    }

    //
    // Common Type functions
    //
    @Override
    public Type toFEELType(TDefinitions model, String typeName) {
        QualifiedName qName = QualifiedName.toQualifiedName(model, typeName);
        return toFEELType(model, qName);
    }

    @Override
    public Type toFEELType(TDefinitions model, QualifiedName typeRef) {
        if (this.dmnModelRepository.isNull(typeRef)) {
            if (dmnTransformer.isStrongTyping()) {
                throw new DMNRuntimeException(String.format("Cannot infer type for typeRef '%s'", typeRef));
            } else {
                return null;
            }
        }

        // Lookup type
        Type type = this.feelTypeMemoizer.get(model, typeRef);
        if (com.gs.dmn.el.analysis.semantics.type.Type.isNull(type)) {
            type = toFEELTypeNoCache(model, typeRef);
            this.feelTypeMemoizer.put(model, typeRef, type);
        }
        return type;
    }

    private Type toFEELTypeNoCache(TDefinitions model, QualifiedName typeRef) {
        // Lookup primitive types
        Type primitiveType = lookupPrimitiveType(typeRef);
        if (primitiveType != null) {
            return primitiveType;
        }
        // Lookup item definitions
        TItemDefinition itemDefinition = this.dmnModelRepository.lookupItemDefinition(model, typeRef);
        if (itemDefinition != null) {
            return toFEELType(itemDefinition);
        }
        throw new DMNRuntimeException(String.format("Cannot map type '%s' to FEEL", typeRef));
    }

    @Override
    public Type toFEELType(TItemDefinition itemDefinition) {
        Type type = this.feelTypeMemoizer.get(itemDefinition);
        if (com.gs.dmn.el.analysis.semantics.type.Type.isNull(type)) {
            type = toFEELTypeNoCache(itemDefinition);
            this.feelTypeMemoizer.put(itemDefinition, type);
        }
        return type;
    }

    private Type toFEELTypeNoCache(TItemDefinition itemDefinition) {
        itemDefinition = this.dmnModelRepository.normalize(itemDefinition);
        TDefinitions model = this.dmnModelRepository.getModel(itemDefinition);
        QualifiedName typeRef = QualifiedName.toQualifiedName(model, itemDefinition.getTypeRef());
        List<TItemDefinition> itemComponent = itemDefinition.getItemComponent();
        TFunctionItem functionItem = itemDefinition.getFunctionItem();
        if (this.dmnModelRepository.isNull(typeRef) && (itemComponent == null || itemComponent.isEmpty()) && functionItem == null) {
            return AnyType.ANY;
        }
        Type type;
        if (!this.dmnModelRepository.isNull(typeRef)) {
            type = toFEELType(model, typeRef);
        } else if (functionItem != null) {
            List<FormalParameter<Type, DMNContext>> formalParameters = makeFormalParameters(model, functionItem.getParameters());
            Type outputType = toFEELType(model, QualifiedName.toName(functionItem.getOutputTypeRef()));
            type = new DMNFunctionType(formalParameters, outputType);
        } else {
            TDefinitions definitions = this.dmnModelRepository.getModel(itemDefinition);
            String modelName = definitions == null ? null : definitions.getName();
            type = new ItemDefinitionType(itemDefinition.getName(), modelName);
            for(TItemDefinition item: itemComponent) {
                ((ItemDefinitionType) type).addMember(item.getName(), Collections.singletonList(item.getLabel()), toFEELType(item));
            }
        }
        if (itemDefinition.isIsCollection()) {
            return new ListType(type);
        } else {
            return type;
        }
    }

    Type lookupPrimitiveType(QualifiedName typeRef) {
        if (this.dmnModelRepository.isNull(typeRef)) {
            return null;
        }
        String importName = typeRef.getNamespace();
        if (DMNVersion.LATEST.getFeelPrefix().equals(importName)) {
            String typeName = typeRef.getLocalPart();
            return FEELTypes.FEEL_NAME_TO_FEEL_TYPE.get(typeName);
        } else if (StringUtils.isBlank(importName)) {
            String typeName = typeRef.getLocalPart();
            return FEELTypes.FEEL_NAME_TO_FEEL_TYPE.get(typeName);
        } else {
            return null;
        }
    }

    private Type makeDSOutputType(TDecisionService decisionService) {
        TDefinitions model = this.dmnModelRepository.getModel(decisionService);
        // Derive from variable
        TInformationItem variable = decisionService.getVariable();
        if (variable != null && variable.getTypeRef() != null) {
            return toFEELType(model, QualifiedName.toName(variable.getTypeRef()));
        }
        // Derive from decisions
        DMNContext context = this.dmnTransformer.makeGlobalContext(decisionService);
        List<TDMNElementReference> outputDecisions = decisionService.getOutputDecision();
        if (outputDecisions.size() == 1) {
            TDecision decision = this.dmnModelRepository.findDecisionByRef(decisionService, outputDecisions.get(0).getHref());
            String decisionName = decision.getName();
            VariableDeclaration declaration = (VariableDeclaration) context.lookupVariableDeclaration(decisionName);
            return declaration.getType();
        } else {
            ContextType type = new ContextType();
            for (TDMNElementReference er: outputDecisions) {
                TDecision decision = this.dmnModelRepository.findDecisionByRef(decisionService, er.getHref());
                String decisionName = decision.getName();
                VariableDeclaration declaration = (VariableDeclaration) context.lookupVariableDeclaration(decisionName);
                type.addMember(decisionName, Collections.emptyList(), declaration.getType());
            }
            return type;
        }
    }

    private FunctionType makeDSVariableType(TDecisionService decisionService) {
        List<FormalParameter<Type, DMNContext>> parameters = this.dmnTransformer.dsFEELParameters(decisionService);
        return new DMNFunctionType(parameters, makeDSOutputType(decisionService), decisionService);
    }

    private Type functionDefinitionType(TDRGElement element, TFunctionDefinition functionDefinition, DMNContext context) {
        TDefinitions model = this.dmnModelRepository.getModel(element);
        TExpression expressionElement = functionDefinition.getExpression();
        if (expressionElement != null) {
            // Calculate body type
            Type bodyType;
            TExpression body = expressionElement;
            QualifiedName typeRef = QualifiedName.toQualifiedName(model, bodyTypeRef(functionDefinition));
            if (!this.dmnModelRepository.isNullOrAny(typeRef)) {
                bodyType = toFEELType(model, typeRef);
            } else {
                DMNContext functionDefinitionContext = this.dmnTransformer.makeFunctionContext(element, functionDefinition, context);
                TFunctionKind kind = functionDefinition.getKind();
                if (this.dmnTransformer.isFEELFunction(kind)) {
                    bodyType = expressionType(element, body, functionDefinitionContext);
                } else if (this.dmnTransformer.isJavaFunction(kind)) {
                    bodyType = AnyType.ANY;
                } else {
                    throw new DMNRuntimeException(String.format("DRGElement '%s': Kind '%s' is not supported yet", element.getName(), kind));
                }
            }
            // Make function type
            List<FormalParameter<Type, DMNContext>> parameters = makeFormalParameters(model, functionDefinition.getFormalParameter());
            if (bodyType != null) {
                return new DMNFunctionType(parameters, bodyType, element, functionDefinition);
            }
        }
        return null;
    }

    private List<FormalParameter<Type, DMNContext>> makeFormalParameters(TDefinitions model, List<TInformationItem> informationItems) {
        List<FormalParameter<Type, DMNContext>> parameters = new ArrayList<>();
        for(TInformationItem param: informationItems) {
            String paramTypeRef = QualifiedName.toName(param.getTypeRef());
            Type paramType = null;
            if (!StringUtils.isEmpty(paramTypeRef)) {
                paramType = toFEELType(model, QualifiedName.toQualifiedName(model, paramTypeRef));
            }
            parameters.add(new FormalParameter<>(param.getName(), paramType));
        }
        return parameters;
    }

    private String bodyTypeRef(TFunctionDefinition functionDefinition) {
        String typeRef = QualifiedName.toName(functionDefinition.getTypeRef());
        if (!StringUtils.isEmpty(typeRef)) {
            return typeRef;
        } else {
            TExpression expression = functionDefinition.getExpression();
            if (expression != null) {
                return QualifiedName.toName(expression.getTypeRef());
            }
        }
        return null;
    }

    private Type literalExpressionType(TNamedElement element, TLiteralExpression body, DMNContext context) {
        Expression<Type, DMNContext> expression = this.feelTranslator.analyzeExpression(body.getText(), context);
        return expression.getType();
    }

    @Override
    public Type externalFunctionReturnFEELType(TNamedElement element, Expression<Type, DMNContext> body) {
        TDefinitions model = this.dmnModelRepository.getModel(element);
        if (body instanceof Context) {
            ContextEntry<Type, DMNContext> javaEntry = ((Context<Type, DMNContext>) body).entry("java");
            if (javaEntry != null) {
                Expression<Type, DMNContext> javaExpression = javaEntry.getExpression();
                if (javaExpression instanceof Context) {
                    ContextEntry<Type, DMNContext> returnTypeEntry = ((Context<Type, DMNContext>) javaExpression).entry("returnType");
                    if (returnTypeEntry != null) {
                        Expression<Type, DMNContext> returnTypeExp = returnTypeEntry.getExpression();
                        if (returnTypeExp instanceof StringLiteral) {
                            String lexeme = ((StringLiteral<Type, DMNContext>) returnTypeExp).getLexeme();
                            String typeName = StringEscapeUtil.stripQuotes(lexeme);
                            return toFEELType(model, QualifiedName.toQualifiedName(model, typeName));
                        }
                    }
                }
            }
        }
        throw new DMNRuntimeException(String.format("Missing returnType in '%s'", body));
    }

    @Override
    public Environment makeEnvironment(TDRGElement element) {
        Environment environment = this.environmentMemoizer.get(element);
        if (environment == null) {
            environment = makeEnvironmentNoCache(element);
            this.environmentMemoizer.put(element, environment);
        }
        return environment;
    }

    private Environment makeEnvironmentNoCache(TDRGElement element) {
        return makeEnvironment(element, true);
    }

    @Override
    public Environment makeEnvironment(TDRGElement element, boolean isRecursive) {
        Environment elementEnvironment = this.environmentFactory.emptyEnvironment();

        // Add declaration for each direct child
        List<DRGElementReference<? extends TDRGElement>> directReferences = this.dmnModelRepository.directDRGElements(element);
        for (DRGElementReference<? extends TDRGElement> reference: directReferences) {
            // Create child environment to infer type if needed
            TDRGElement child = reference.getElement();
            Declaration declaration = makeDeclaration(element, elementEnvironment, child);
            addDeclaration(elementEnvironment, declaration, element, child);
        }

        // Add it to cache to avoid infinite loops
        this.environmentMemoizer.put(element, elementEnvironment);
        // Add declaration of element to support recursion
        if (isRecursive) {
            Declaration declaration = makeDeclaration(element, elementEnvironment, element);
            addDeclaration(elementEnvironment, declaration, element, element);
        }

        // Add declaration for parameters
        if (element instanceof TBusinessKnowledgeModel) {
            TDefinitions definitions = this.dmnModelRepository.getModel(element);
            TFunctionDefinition functionDefinition = ((TBusinessKnowledgeModel) element).getEncapsulatedLogic();
            if (functionDefinition != null) {
                for (TInformationItem p: functionDefinition.getFormalParameter()) {
                    String paramTypeRef = QualifiedName.toName(p.getTypeRef());
                    Type paramType = null;
                    if (!StringUtils.isEmpty(paramTypeRef)) {
                        paramType = this.dmnTransformer.toFEELType(definitions, QualifiedName.toQualifiedName(definitions, paramTypeRef));
                    }
                    elementEnvironment.addDeclaration(this.environmentFactory.makeVariableDeclaration(p.getName(), paramType));
                }
            }
        }
        // No need to add parameters for DecisionService, added in the first step

        return elementEnvironment;
    }

    protected Declaration makeDeclaration(TDRGElement parent, Environment parentEnvironment, TDRGElement child) {
        if (parent == null || child == null) {
            throw new IllegalArgumentException("Cannot add declaration for null DRG element");
        }

        Declaration declaration;
        if (child instanceof TInputData) {
            declaration = makeVariableDeclaration(child, ((TInputData) child).getVariable());
        } else if (child instanceof TBusinessKnowledgeModel) {
            declaration = makeInvocableDeclaration((TBusinessKnowledgeModel) child);
        } else if (child instanceof TDecision) {
            declaration = makeVariableDeclaration(child, ((TDecision) child).getVariable());
        } else if (child instanceof TDecisionService) {
            declaration = makeInvocableDeclaration((TDecisionService) child);
        } else {
            throw new UnsupportedOperationException(String.format("'%s' is not supported yet", child.getClass().getSimpleName()));
        }
        return declaration;
    }

    protected void addDeclaration(Environment environment, Declaration declaration, TDRGElement parent, TDRGElement child) {
        Type type = declaration.getType();
        String importName = this.dmnModelRepository.findChildImportName(parent, child);
        if (ImportPath.isEmpty(importName)) {
            environment.addDeclaration(declaration);
        } else {
            Declaration importDeclaration = environment.lookupLocalVariableDeclaration(importName);
            if (importDeclaration == null) {
                ImportContextType contextType = new ImportContextType(importName);
                contextType.addMember(declaration.getName(), new ArrayList<>(), type);
                contextType.addMemberReference(declaration.getName(), this.dmnModelRepository.makeDRGElementReference(importName, child));
                importDeclaration = this.environmentFactory.makeVariableDeclaration(importName, contextType);
                environment.addDeclaration(importDeclaration);
            } else if (importDeclaration instanceof VariableDeclaration) {
                Type importType = importDeclaration.getType();
                if (importType instanceof ImportContextType) {
                    ((ImportContextType) importType).addMember(declaration.getName(), new ArrayList<>(), type);
                    ((ImportContextType) importType).addMemberReference(declaration.getName(), this.dmnModelRepository.makeDRGElementReference(importName, child));
                } else {
                    throw new DMNRuntimeException(String.format("Cannot process declaration for '%s.%s'", importName, declaration.getName()));
                }
            } else {
                throw new DMNRuntimeException(String.format("Cannot process declaration for '%s.%s'", importName, declaration.getName()));
            }
        }
    }

    //
    // Decision Table
    //
    @Override
    public Environment makeUnaryTestEnvironment(TDRGElement element, Expression<Type, DMNContext> inputExpression) {
        Environment environment = this.environmentFactory.makeEnvironment(inputExpression);
        if (inputExpression != null) {
            environment.addDeclaration(this.environmentFactory.makeVariableDeclaration(DMNContext.INPUT_ENTRY_PLACE_HOLDER, inputExpression.getType()));
        }
        return environment;
    }

    //
    // Function Definition
    //
    @Override
    public Environment makeFunctionDefinitionEnvironment(TNamedElement element, TFunctionDefinition functionDefinition) {
        TDefinitions model = this.dmnModelRepository.getModel(element);
        Environment environment = this.environmentFactory.emptyEnvironment();
        for (TInformationItem p: functionDefinition.getFormalParameter()) {
            String typeRef = QualifiedName.toName(p.getTypeRef());
            Type type = null;
            if (!StringUtils.isEmpty(typeRef)) {
                type = this.dmnTransformer.toFEELType(model, QualifiedName.toQualifiedName(model, typeRef));
            }
            environment.addDeclaration(this.environmentFactory.makeVariableDeclaration(p.getName(), type));
        }
        return environment;
    }

    @Override
    public Declaration makeVariableDeclaration(TDRGElement element, TInformationItem variable) {
        // Check variable
        String name = element.getName();
        if (StringUtils.isBlank(name) && variable != null) {
            name = variable.getName();
        }
        if (StringUtils.isBlank(name) || variable == null) {
            throw new DMNRuntimeException(String.format("Name and variable cannot be null. Found '%s' and '%s'", name, variable));
        }

        Type variableType = this.dmnTransformer.drgElementVariableFEELType(element);
        return this.environmentFactory.makeVariableDeclaration(name, variableType);
    }

    protected Declaration makeInvocableDeclaration(TInvocable invocable) {
        if (invocable instanceof TBusinessKnowledgeModel) {
            return makeBKMDeclaration((TBusinessKnowledgeModel) invocable);
        } else if (invocable instanceof TDecisionService) {
            return makeDSDeclaration((TDecisionService) invocable);
        } else {
            throw new UnsupportedOperationException(String.format("'%s' is not supported yet", invocable.getClass().getSimpleName()));
        }
    }

    private Declaration makeDSDeclaration(TDecisionService ds) {
        TInformationItem variable = ds.getVariable();
        String name = ds.getName();
        if (StringUtils.isBlank(name) && variable != null) {
            name = variable.getName();
        }
        if (StringUtils.isBlank(name)) {
            throw new DMNRuntimeException(String.format("Name and variable cannot be null. Found '%s' and '%s'", name, variable));
        }
        FunctionType serviceType = (FunctionType) this.drgElementVariableFEELType(ds);
        return this.environmentFactory.makeVariableDeclaration(name, serviceType);
    }

    private Declaration makeBKMDeclaration(TBusinessKnowledgeModel bkm) {
        TInformationItem variable = bkm.getVariable();
        String name = bkm.getName();
        if (StringUtils.isBlank(name) && variable != null) {
            name = variable.getName();
        }
        if (StringUtils.isBlank(name)) {
            throw new DMNRuntimeException(String.format("Name and variable cannot be null. Found '%s' and '%s'", name, variable));
        }
        List<FormalParameter<Type, DMNContext>> parameters = this.dmnTransformer.bkmFEELParameters(bkm);
        Type returnType = this.dmnTransformer.drgElementOutputFEELType(bkm);
        return this.environmentFactory.makeVariableDeclaration(name, new DMNFunctionType(parameters, returnType, bkm));
    }

    //
    // Context
    //
    @Override
    public Pair<DMNContext, Map<TContextEntry, Expression<Type, DMNContext>>> makeContextEnvironment(TDRGElement element, TContext context, DMNContext parentContext) {
        DMNContext localContext = this.dmnTransformer.makeLocalContext(element, parentContext);
        Map<TContextEntry, Expression<Type, DMNContext>> literalExpressionMap = new LinkedHashMap<>();
        for(TContextEntry entry: context.getContextEntry()) {
            TInformationItem variable = entry.getVariable();
            TExpression expression = entry.getExpression();
            Expression<Type, DMNContext> feelExpression = null;
            if (expression instanceof TLiteralExpression) {
                feelExpression = this.feelTranslator.analyzeExpression(((TLiteralExpression) expression).getText(), localContext);
                literalExpressionMap.put(entry, feelExpression);
            }
            if (variable != null) {
                String name = variable.getName();
                Type entryType;
                if (expression instanceof TLiteralExpression) {
                    entryType = entryType(element, entry, expression, feelExpression);
                } else {
                    entryType = entryType(element, entry, localContext);
                }
                localContext.addDeclaration(this.environmentFactory.makeVariableDeclaration(name, entryType));
            }
        }
        return new Pair<>(localContext, literalExpressionMap);
    }

    @Override
    public Type entryType(TDRGElement element, TContextEntry entry, TExpression expression, Expression<Type, DMNContext> feelExpression) {
        TDefinitions model = this.dmnModelRepository.getModel(element);
        TInformationItem variable = entry.getVariable();
        Type entryType = variableType(element, variable);
        if (entryType != null) {
            return entryType;
        }
        QualifiedName typeRef = expression == null ? null : QualifiedName.toQualifiedName(model, expression.getTypeRef());
        if (!this.dmnModelRepository.isNullOrAny(typeRef)) {
            entryType = this.dmnTransformer.toFEELType(model, typeRef);
        }
        if (entryType == null) {
            entryType = feelExpression.getType();
        }
        return entryType;
    }

    @Override
    public Type entryType(TDRGElement element, TContextEntry entry, DMNContext localContext) {
        TInformationItem variable = entry.getVariable();
        Type type = variableType(element, variable);
        if (!com.gs.dmn.el.analysis.semantics.type.Type.isNull(type)) {
            return type;
        }
        // Infer type from expression
        type = this.dmnTransformer.expressionType(element, entry.getExpression(), localContext);
        return com.gs.dmn.el.analysis.semantics.type.Type.isNull(type) ? AnyType.ANY : type;
    }

    private Type variableType(TNamedElement element, TInformationItem variable) {
        TDefinitions model = this.dmnModelRepository.getModel(element);
        if (variable != null) {
            QualifiedName typeRef = QualifiedName.toQualifiedName(model, variable.getTypeRef());
            if (!this.dmnModelRepository.isNullOrAny(typeRef)) {
                return this.dmnTransformer.toFEELType(model, typeRef);
            }
        }
        return null;
    }

    //
    // Relation
    //
    @Override
    public Environment makeRelationEnvironment(TNamedElement element, TRelation relation) {
        TDefinitions model = this.dmnModelRepository.getModel(element);
        Environment relationEnvironment = this.environmentFactory.emptyEnvironment();
        for(TInformationItem column: relation.getColumn()) {
            QualifiedName typeRef = QualifiedName.toQualifiedName(model, column.getTypeRef());
            if (!this.dmnModelRepository.isNull(typeRef)) {
                String name = column.getName();
                relationEnvironment.addDeclaration(this.environmentFactory.makeVariableDeclaration(name, this.dmnTransformer.toFEELType(model, typeRef)));
            }
        }
        return relationEnvironment;
    }
}
