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
package com.gs.dmn.feel.analysis.semantics.type;

import com.gs.dmn.feel.analysis.syntax.ast.expression.function.FormalParameter;
import com.gs.dmn.feel.analysis.syntax.ast.expression.function.ParameterConversions;
import com.gs.dmn.feel.analysis.syntax.ast.expression.function.ParameterTypes;
import com.gs.dmn.runtime.Pair;
import org.omg.spec.dmn._20191111.model.TBusinessKnowledgeModel;
import org.omg.spec.dmn._20191111.model.TDRGElement;
import org.omg.spec.dmn._20191111.model.TFunctionDefinition;
import org.omg.spec.dmn._20191111.model.TFunctionKind;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DMNFunctionType<C> extends FunctionType<C> {
    private final TDRGElement drgElement;
    private final TFunctionDefinition functionDefinition;

    public DMNFunctionType(List<FormalParameter<C>> formalParameters, Type outputType) {
        this(formalParameters, outputType, null);
    }

    public DMNFunctionType(List<FormalParameter<C>> parameters, Type returnType, TDRGElement drgElement) {
        this(parameters, returnType, drgElement, drgElement instanceof TBusinessKnowledgeModel ? ((TBusinessKnowledgeModel) drgElement).getEncapsulatedLogic() : null);
    }

    public DMNFunctionType(List<FormalParameter<C>> parameters, Type returnType, TDRGElement drgElement, TFunctionDefinition functionDefinition) {
        super(parameters, returnType);
        this.drgElement = drgElement;
        this.functionDefinition = functionDefinition;
    }

    public TDRGElement getDRGElement() {
        return this.drgElement;
    }

    public DMNFunctionType<C> attachElement(TDRGElement element) {
        return new DMNFunctionType<>(this.getParameters(), this.getReturnType(), element);
    }

    public TFunctionKind getKind() {
        return this.functionDefinition == null ? null : this.functionDefinition.getKind();
    }

    @Override
    protected boolean equivalentTo(Type other) {
        return other instanceof DMNFunctionType
                && Type.equivalentTo(this.returnType, ((FunctionType<C>) other).returnType)
                && Type.equivalentTo(this.parameterTypes, ((FunctionType<C>) other).parameterTypes);
    }

    @Override
    protected boolean conformsTo(Type other) {
        // “contravariant function argument type” and “covariant function return type”
        return other instanceof FunctionType
                && Type.conformsTo(this.returnType, ((FunctionType<C>) other).returnType)
                && Type.conformsTo(((FunctionType<C>) other).parameterTypes, this.parameterTypes);
    }

    @Override
    public boolean match(ParameterTypes<C> parameterTypes) {
        List<FormalParameter<C>> parameters = getParameters();
        if (parameters.size() != parameterTypes.size()) {
            return false;
        }
        return parameterTypes.compatible(parameters);
    }


    @Override
    protected List<Pair<ParameterTypes<C>, ParameterConversions<C>>> matchCandidates(List<Type> argumentTypes) {
        // check size constraint
        if (argumentTypes.size() != this.parameterTypes.size()) {
            return new ArrayList<>();
        }
        // calculate candidates
        return calculateCandidates(this.parameterTypes, argumentTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DMNFunctionType that = (DMNFunctionType) o;

        if (this.parameters != null ? !this.parameters.equals(that.parameters) : that.parameters != null) {
            return false;
        }
        return Objects.equals(this.returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        int result = this.parameters != null ? this.parameters.hashCode() : 0;
        result = 31 * result + (this.returnType != null ? this.returnType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String types = this.parameters.stream().map(p -> p == null ? "null" : p.toString()).collect(Collectors.joining(", "));
        return String.format("%s(%s, %s)", getClass().getSimpleName(), types, this.returnType);
    }
}
