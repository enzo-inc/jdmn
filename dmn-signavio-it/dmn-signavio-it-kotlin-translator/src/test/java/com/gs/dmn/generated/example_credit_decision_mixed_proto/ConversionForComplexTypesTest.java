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
package com.gs.dmn.generated.example_credit_decision_mixed_proto;

import com.gs.dmn.generated.example_credit_decision_mixed_proto.proto.GenerateOutputDataRequest;
import com.gs.dmn.generated.example_credit_decision_mixed_proto.type.Applicant;
import com.gs.dmn.generated.example_credit_decision_mixed_proto.type.ApplicantImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConversionForComplexTypesTest {
    @Test
    public void testDefaultValues() {
        GenerateOutputDataRequest request = GenerateOutputDataRequest.newBuilder().build();
        assertNotNull(request.getApplicant());
        assertEquals(0.0, request.getCurrentRiskAppetite(), 0.001);
    }

    @Test
    public void testConvertMethodsWhenNull() {
        com.gs.dmn.generated.example_credit_decision_mixed_proto.proto.Applicant protoApplicant = Applicant.toProto((Applicant) null);
        assertNotNull(null, protoApplicant);
        assertEquals(0, protoApplicant.getAge(), 0.0001);
    }

    @Test
    public void testConvertMethodsWhenMissingProperties() {
        Applicant applicant = new ApplicantImpl();
        com.gs.dmn.generated.example_credit_decision_mixed_proto.proto.Applicant protoApplicant = Applicant.toProto(applicant);
        assertNotNull(protoApplicant);
        assertEquals(0, protoApplicant.getAge(), 0.001);
        assertEquals("", protoApplicant.getName());
        assertEquals(Collections.emptyList(), protoApplicant.getPriorIssuesList());
    }
}
