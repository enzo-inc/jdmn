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
package com.gs.dmn.generated.cd_primitive_type_inputs_sfeel_input_entries_compound_output_first_hit_policy;

import com.gs.dmn.generated.AbstractHandwrittenDecisionTest;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandwrittenCompoundOutputCompoundDecisionTest extends AbstractHandwrittenDecisionTest {
    private final CompoundOutputCompoundDecision decision = new CompoundOutputCompoundDecision();

    @Test
    public void testApply() throws Exception {
        check("true", "e1", "1", "a", "r11", "r12");
        check("true", "e2", "1", "a", "r21", "r22");
    }

    private void check(String booleanInput, String enumerationInput, String numberInput, String textInput, String expectedFirstOutput, String expectedSecondOutput) {
        com.gs.dmn.generated.cd_primitive_type_inputs_sfeel_input_entries_compound_output_first_hit_policy.type.CompoundOutputCompoundDecision output = applyDecision(booleanInput, enumerationInput, numberInput, textInput);
        assertEquals(expectedFirstOutput, output.getFirstOutput());
        assertEquals(expectedSecondOutput, output.getSecondOutput());
    }

    @Override
    protected void applyDecision() {
        applyDecision("true", "e1", "1", "a");
    }

    private com.gs.dmn.generated.cd_primitive_type_inputs_sfeel_input_entries_compound_output_first_hit_policy.type.CompoundOutputCompoundDecision applyDecision(String booleanInput, String enumerationInput, String numberInput, String textInput) {
        Map<String, String> input = new LinkedHashMap<>();
        input.put("BooleanInput", booleanInput);
        input.put("EnumerationInput", enumerationInput);
        input.put("DD2 Number Input", numberInput);
        input.put("DD1 Text Input", textInput);
        return decision.applyMap(input, context);
    }
}