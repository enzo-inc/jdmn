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
package com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy;

import com.gs.dmn.generated.AbstractHandwrittenDecisionTest;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandwrittenDecisionTest extends AbstractHandwrittenDecisionTest {
    private final Decision decision = new Decision();

    @Test
    public void testApply() {
        List<com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision> output = applyDecision("1", "1");
        assertEquals("r2, r3, r4, r5",
                output.stream().map(com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision::getOutput1).collect(Collectors.joining(", ")));
        assertEquals("r6, r7, r8, r9",
                output.stream().map(com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision::getOutput2).collect(Collectors.joining(", ")));

        output = applyDecision("1", null);
        assertEquals("r2, r4",
                output.stream().map(com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision::getOutput1).collect(Collectors.joining(", ")));
        assertEquals("r7, r9",
                output.stream().map(com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision::getOutput2).collect(Collectors.joining(", ")));

        output = applyDecision(null, "1");
        assertEquals("r2, r3",
                output.stream().map(com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision::getOutput1).collect(Collectors.joining(", ")));
        assertEquals("r8, r9",
                output.stream().map(com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision::getOutput2).collect(Collectors.joining(", ")));

        output = applyDecision(null, null);
        assertEquals("r2",
                output.stream().map(com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision::getOutput1).collect(Collectors.joining(", ")));
        assertEquals("r9",
                output.stream().map(com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision::getOutput2).collect(Collectors.joining(", ")));
    }

    @Override
    protected void applyDecision() {
        applyDecision("1", "1");
    }

    private List<com.gs.dmn.generated.sd_primitive_type_inputs_compound_output_output_order_hit_policy.type.Decision> applyDecision(String numberInput, String textInput) {
        Map<String, String> input = new LinkedHashMap<>();
        input.put("NumberInput", numberInput);
        input.put("TextInput", textInput);
        return decision.applyMap(input, context);
    }
}