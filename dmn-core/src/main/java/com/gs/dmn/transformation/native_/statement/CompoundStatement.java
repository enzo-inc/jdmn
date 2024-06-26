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
package com.gs.dmn.transformation.native_.statement;

import com.gs.dmn.runtime.DMNRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class CompoundStatement extends Statement {
    private final List<Statement> statements = new ArrayList<>();

    public CompoundStatement() {
    }

    public List<Statement> getStatements() {
        return this.statements;
    }

    public void add(Statement statement) {
        this.statements.add(statement);
    }

    @Override
    public String getText() {
        throw new DMNRuntimeException("Not supported");
    }

    @Override
    public String toString() {
        return statements.toString();
    }
}
