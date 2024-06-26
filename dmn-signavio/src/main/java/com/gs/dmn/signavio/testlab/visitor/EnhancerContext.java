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
package com.gs.dmn.signavio.testlab.visitor;

import com.gs.dmn.ast.TItemDefinition;
import com.gs.dmn.signavio.testlab.TestLab;

public class EnhancerContext {
    private final TestLab testLab;
    private int testCaseIndex;
    private TItemDefinition type;

    public EnhancerContext(TestLab testLab) {
        this.testLab = testLab;
    }

    public TestLab getTestLab() {
        return this.testLab;
    }

    public int getTestCaseIndex() {
        return this.testCaseIndex;
    }

    public EnhancerContext withTestCaseIndex(int testCaseIndex) {
        this.testCaseIndex = testCaseIndex;
        return this;
    }

    public TItemDefinition getType() {
        return type;
    }

    public EnhancerContext withType(TItemDefinition type) {
        this.type = type;
        return this;
    }
}
