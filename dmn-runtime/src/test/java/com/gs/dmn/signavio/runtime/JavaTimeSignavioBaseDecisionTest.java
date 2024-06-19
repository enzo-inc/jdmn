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
package com.gs.dmn.signavio.runtime;

import com.gs.dmn.runtime.annotation.DRGElement;
import com.gs.dmn.runtime.annotation.Rule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class JavaTimeSignavioBaseDecisionTest {
    private final JavaTimeSignavioBaseDecision baseDecision = new JavaTimeSignavioBaseDecision();

    @Test
    public void testGetDRGElementAnnotation() throws Exception {
        DRGElement drgElementAnnotation = baseDecision.getDRGElementAnnotation();
        assertNull(drgElementAnnotation);
    }

    @Test
    public void testGetRuleAnnotation() throws Exception {
        Rule ruleAnnotation = baseDecision.getRuleAnnotation(0);
        assertNull(ruleAnnotation);
    }

}