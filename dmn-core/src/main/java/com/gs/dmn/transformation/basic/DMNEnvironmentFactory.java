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

import com.gs.dmn.feel.analysis.semantics.environment.Declaration;
import com.gs.dmn.feel.analysis.semantics.environment.Environment;
import com.gs.dmn.feel.analysis.semantics.environment.EnvironmentFactory;
import com.gs.dmn.feel.analysis.semantics.type.Type;
import com.gs.dmn.feel.analysis.syntax.ast.expression.Expression;
import com.gs.dmn.runtime.Pair;
import org.omg.spec.dmn._20180521.model.*;

import java.util.Map;

public interface DMNEnvironmentFactory {
    Environment makeEnvironment(TDRGElement element);

    Environment makeEnvironment(TDRGElement element, Environment parentEnvironment);

    //
    // Decision Table
    //
    Environment makeInputEntryEnvironment(TDRGElement element, Expression inputExpression);

    Environment makeOutputEntryEnvironment(TDRGElement element, EnvironmentFactory environmentFactory);

    //
    // Function Definition
    //
    Environment makeFunctionDefinitionEnvironment(TNamedElement element, TFunctionDefinition functionDefinition, Environment parentEnvironment);

    Declaration makeVariableDeclaration(TDRGElement element, TInformationItem variable);

    //
    // Context
    //
    Pair<Environment, Map<TContextEntry, Expression>> makeContextEnvironment(TDRGElement element, TContext context, Environment parentEnvironment);

    Type entryType(TDRGElement element, TContextEntry entry, Environment contextEnvironment);

    Type entryType(TDRGElement element, TContextEntry entry, TExpression expression, Expression feelExpression);

    //
    // Relation
    //
    Environment makeRelationEnvironment(TNamedElement element, TRelation relation, Environment environment);

}
