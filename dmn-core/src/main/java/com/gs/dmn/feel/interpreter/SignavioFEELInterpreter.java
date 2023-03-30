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
package com.gs.dmn.feel.interpreter;

import com.gs.dmn.feel.analysis.FEELAnalyzerImpl;
import com.gs.dmn.runtime.interpreter.DMNInterpreter;

public class SignavioFEELInterpreter<NUMBER, DATE, TIME, DATE_TIME, DURATION> extends AbstractFEELInterpreter<NUMBER, DATE, TIME, DATE_TIME, DURATION> {
    public SignavioFEELInterpreter(DMNInterpreter<NUMBER, DATE, TIME, DATE_TIME, DURATION> dmnInterpreter) {
        super(dmnInterpreter, new FEELAnalyzerImpl(dmnInterpreter.getBasicDMNTransformer()));
    }

    @Override
    protected AbstractFEELInterpreterVisitor<NUMBER, DATE, TIME, DATE_TIME, DURATION> makeVisitor(DMNInterpreter<NUMBER, DATE, TIME, DATE_TIME, DURATION> dmnInterpreter) {
        return new SignavioFEELInterpreterVisitor<>(dmnInterpreter);
    }
}
