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
package com.gs.dmn.feel.analysis.semantics;

import com.gs.dmn.feel.analysis.semantics.environment.Declaration;
import com.gs.dmn.feel.analysis.semantics.environment.StandardEnvironmentFactory;
import com.gs.dmn.feel.analysis.semantics.type.FunctionType;
import com.gs.dmn.feel.analysis.semantics.type.ListType;
import com.gs.dmn.feel.analysis.semantics.type.Type;
import com.gs.dmn.feel.analysis.syntax.ast.expression.Expression;
import com.gs.dmn.feel.analysis.syntax.ast.expression.function.*;
import com.gs.dmn.feel.analysis.syntax.ast.expression.literal.ListLiteral;
import com.gs.dmn.runtime.DMNContext;
import com.gs.dmn.runtime.DMNRuntimeException;
import com.gs.dmn.runtime.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.gs.dmn.feel.analysis.semantics.type.AnyType.ANY;

public class FunctionInvocationUtils {
    static void deriveType(FunctionInvocation<DMNContext> element, DMNContext context, String functionName) {
        DeclarationMatch declarationMatch = functionResolution(element, context, functionName);
        Declaration functionDeclaration = declarationMatch.getDeclaration();
        Type functionType = refineFunctionType(element, functionDeclaration);
        element.getFunction().setType(functionType);

        setInvocationType(element, functionType);
        element.getParameters().setParameterConversions(declarationMatch.getParameterConversions());
        element.getParameters().setConvertedParameterTypes(declarationMatch.getParameterTypes());
    }

    private static Type refineFunctionType(FunctionInvocation<DMNContext> element, Declaration functionDeclaration) {
        String functionName = functionDeclaration.getName();
        Parameters<DMNContext> parameters = element.getParameters();
        if("sublist".equals(functionName)) {
            Type listType = parameters.getParameterType(0, "list");
            return StandardEnvironmentFactory.makeSublistBuiltInFunctionType(listType);
        } else if("append".equals(functionName)) {
            FormalParameter<DMNContext> formalParameter = ((FunctionType) functionDeclaration.getType()).getParameters().get(1);
            String name = formalParameter.getName();
            if ("item".equals(name)) {
                Type listType = parameters.getParameterType(0, "list");
                Type itemType = parameters.getParameterType(1, "item");
                return StandardEnvironmentFactory.makeAppendBuiltinFunctionType(listType, itemType);
            } else {
                Type listType = parameters.getParameterType(0, "list");
                Type elementType = parameters.getParameterType(1, "element");
                return StandardEnvironmentFactory.makeSignavioAppendBuiltinFunctionType(listType, elementType);
            }
        } else if("concatenate".equals(functionName)) {
            Type listType = parameters.getParameterType(0, "list");
            return StandardEnvironmentFactory.makeConcatenateBuiltinFunctionType(listType);
        } else if("insert before".equals(functionName)) {
            Type listType = parameters.getParameterType(0, "list");
            Type newItemType = parameters.getParameterType(2, "'new item'");
            return StandardEnvironmentFactory.makeInsertBeforeBuiltinFunctionType(listType, newItemType);
        } else if("remove".equals(functionName)) {
            FormalParameter<DMNContext> formalParameter = ((FunctionType) functionDeclaration.getType()).getParameters().get(1);
            String name = formalParameter.getName();
            if ("position".equals(name)) {
                Type listType = parameters.getParameterType(0, "list");
                return StandardEnvironmentFactory.makeRemoveBuiltinFunctionType(listType);
            } else {
                Type listType = parameters.getParameterType(0, "list");
                Type elementType = parameters.getParameterType(1, "element");
                return StandardEnvironmentFactory.makeSignavioRemoveBuiltinFunctionType(listType, elementType);
            }
        } else if("reverse".equals(functionName)) {
            Type listType = parameters.getParameterType(0, "list");
            return StandardEnvironmentFactory.makeReverseBuiltinFunctionType(listType);
        } else if("index of".equals(functionName)) {
            Type listType = parameters.getParameterType(0, "list");
            Type matchType = parameters.getParameterType(1, "match");
            return StandardEnvironmentFactory.makeIndexOfBuiltinFunctionType(listType, matchType);
        } else if("distinct values".equals(functionName)) {
            Type listType = parameters.getParameterType(0, "list");
            return StandardEnvironmentFactory.makeDistinctValuesBuiltinFunctionType(listType);
        } else if("union".equals(functionName)) {
            Type listType = parameters.getParameterType(0, "list1");
            return StandardEnvironmentFactory.makeUnionBuiltinFunctionType(listType);
        } else if("flatten".equals(functionName)) {
            Expression<DMNContext> inputListParameter = parameters.getParameter(0, "list");
            Type elementType = nestedElementType(inputListParameter);
            return StandardEnvironmentFactory.makeFlattenBuiltinFunctionType(new ListType(elementType));
        } else if ("sort".equals(functionName)) {
            Type listType = parameters.getParameterType(0, "list");
            Type functionType = parameters.getParameterType(1, "function");
            return StandardEnvironmentFactory.makeSortBuiltinFunctionType(listType, functionType);

        } else if("appendAll".equals(functionName)) {
            Type list1Type = parameters.getParameterType(0, "list1");
            return StandardEnvironmentFactory.makeSignavioAppendAllBuiltinFunctionType(list1Type);
        } else if("removeAll".equals(functionName)) {
            Type list1Type = parameters.getParameterType(0, "list1");
            return StandardEnvironmentFactory.makeSignavioRemoveAllBuiltinFunctionType(list1Type);
        } else {
            return functionDeclaration.getType();
        }
    }

    private static Type nestedElementType(Expression<DMNContext> expression) {
        List<Type> primitiveTypes = new ArrayList<>();
        collectPrimitiveTypes(expression, primitiveTypes);
        Type upperBound = null;
        for (Type type: primitiveTypes) {
            if (upperBound == null) {
                upperBound = type;
            } else {
                if (!Type.conformsTo(type, upperBound)) {
                    if (Type.conformsTo(upperBound, type)) {
                        upperBound = type;
                    } else {
                        upperBound = ANY;
                    }
                }
            }
        }
        if (upperBound == null) {
            upperBound = ANY;
        }
        return upperBound;
    }

    private static void collectPrimitiveTypes(Expression<DMNContext> expression, List<Type> types) {
        if (expression instanceof ListLiteral) {
            List<Expression<DMNContext>> expressionList = ((ListLiteral) expression).getExpressionList();
            for (Expression<DMNContext> exp: expressionList) {
                collectPrimitiveTypes(exp, types);
            }
        } else {
            Type type = expression.getType();
            while (type instanceof ListType) {
                type = ((ListType) type).getElementType();
            }
            if (type != null) {
                types.add(type);
            }
        }
    }

    private static DeclarationMatch functionResolution(FunctionInvocation<DMNContext> element, DMNContext context, String name) {
        ParameterTypes<DMNContext> parameterTypes = element.getParameters().getSignature();
        List<DeclarationMatch> functionMatches = findFunctionMatches(element, context, name, parameterTypes);
        if (functionMatches.isEmpty()) {
            throw new DMNRuntimeException(String.format("Cannot resolve function '%s(%s)'", name, parameterTypes));
        } else if (functionMatches.size() == 1) {
            return functionMatches.get(0);
        } else {
            throw new DMNRuntimeException(String.format("Found %d matches for function '%s(%s)' %s", functionMatches.size(), name, parameterTypes, functionMatches));
        }
    }

    static void setInvocationType(FunctionInvocation<DMNContext> element, Type functionType) {
        if (functionType instanceof FunctionType) {
            element.setType(((FunctionType) functionType).getReturnType());
        } else {
            throw new DMNRuntimeException(String.format("Expected function type for '%s'. Found '%s'", element.getFunction(), functionType));
        }
    }

    private static List<DeclarationMatch> findFunctionMatches(FunctionInvocation<DMNContext> element, DMNContext context, String name, ParameterTypes<DMNContext> parameterTypes) {
        List<Declaration> declarations = context.lookupFunctionDeclaration(name);
        // Phase 1: Look for candidates without conversions
        List<DeclarationMatch> matches = new ArrayList<>();
        for (Declaration declaration : declarations) {
            FunctionType functionType = (FunctionType) declaration.getType();
            if (functionType.match(parameterTypes)) {
                // Exact match. no conversion required
                DeclarationMatch declarationMatch = makeDeclarationMatch(element, declaration);
                matches.add(declarationMatch);
                return matches;
            }
        }
        // Phase 2: Check for candidates after applying conversions when types do not conform
        for (Declaration declaration : declarations) {
            FunctionType functionType = (FunctionType) declaration.getType();
            List<Pair<ParameterTypes<DMNContext>, ParameterConversions<DMNContext>>> candidates = functionType.matchCandidates(parameterTypes);
            for (Pair<ParameterTypes<DMNContext>, ParameterConversions<DMNContext>> candidate: candidates) {
                ParameterTypes<DMNContext> candidateParameterTypes = candidate.getLeft();
                ParameterConversions<DMNContext> candidateParameterConversions = candidate.getRight();
                if (element.getParameters() instanceof NamedParameters) {
                    // candidates are always positional
                    List<FormalParameter<DMNContext>> formalParameters = functionType.getParameters();
                    ParameterTypes<DMNContext> matchParameterTypes = NamedParameterTypes.toNamedParameterTypes((PositionalParameterTypes) candidateParameterTypes, formalParameters);
                    NamedParameterConversions<DMNContext> matchParameterConversions = NamedParameterConversions.toNamedParameterConversions((PositionalParameterConversions) candidateParameterConversions, formalParameters);
                    matches.add(makeDeclarationMatch(declaration, matchParameterTypes, matchParameterConversions));
                } else {
                    matches.add(makeDeclarationMatch(declaration, candidateParameterTypes, candidateParameterConversions));
                }
            }
        }
        // Phase 3: Filter candidates without conformTo conversion
        List<DeclarationMatch> noConformTo = matches.stream().filter(m -> !m.getParameterConversions().hasConversion(ConversionKind.CONFORMS_TO)).collect(Collectors.toList());
        if (noConformTo.size() != 0) {
            return noConformTo;
        }
        // Phase 3: Return candidates with conformsTo
        return matches;
    }

    private static DeclarationMatch makeDeclarationMatch(FunctionInvocation<DMNContext> element, Declaration functionDeclaration) {
        FunctionType functionType = (FunctionType) functionDeclaration.getType();
        Parameters parameters = element.getParameters();
        ParameterTypes newParameterTypes = parameters.getSignature();
        if (parameters instanceof NamedParameters) {
            NamedParameterConversions parameterConversions = new NamedParameterConversions(functionType.getParameters());
            return makeDeclarationMatch(functionDeclaration, newParameterTypes, parameterConversions);
        } else {
            PositionalParameterConversions parameterConversions = new PositionalParameterConversions(functionType.getParameterTypes());
            return makeDeclarationMatch(functionDeclaration, newParameterTypes, parameterConversions);
        }
    }

    private static DeclarationMatch makeDeclarationMatch(Declaration declaration, ParameterTypes newParameterTypes, ParameterConversions parameterConversions) {
        return new DeclarationMatch(declaration, newParameterTypes, parameterConversions);
    }
}
