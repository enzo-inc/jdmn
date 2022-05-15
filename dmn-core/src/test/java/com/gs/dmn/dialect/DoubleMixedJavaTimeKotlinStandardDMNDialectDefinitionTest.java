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
package com.gs.dmn.dialect;

import com.gs.dmn.feel.lib.DoubleMixedJavaTimeFEELLib;
import com.gs.dmn.feel.synthesis.type.DoubleMixedJavaTimeKotlinNativeTypeFactory;
import com.gs.dmn.runtime.DoubleMixedJavaTimeDMNBaseDecision;
import com.gs.dmn.runtime.interpreter.StandardDMNInterpreter;
import com.gs.dmn.tck.ast.TestCases;
import com.gs.dmn.transformation.DMNToKotlinTransformer;
import com.gs.dmn.transformation.basic.BasicDMNToKotlinTransformer;

import javax.xml.datatype.Duration;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

public class DoubleMixedJavaTimeKotlinStandardDMNDialectDefinitionTest extends AbstractStandardDMNDialectDefinitionTest<Double, LocalDate, OffsetTime, ZonedDateTime, Duration> {
    @Override
    protected DMNDialectDefinition<Double, LocalDate, OffsetTime, ZonedDateTime, Duration, TestCases> makeDialect() {
        return new DoubleMixedJavaTimeKotlinStandardDMNDialectDefinition();
    }

    @Override
    protected String getExpectedDMNInterpreterClass() {
        return StandardDMNInterpreter.class.getName();
    }

    @Override
    protected String getExpectedDMNToNativeTransformerClass() {
        return DMNToKotlinTransformer.class.getName();
    }

    @Override
    protected String getBasicTransformerClass() {
        return BasicDMNToKotlinTransformer.class.getName();
    }

    @Override
    protected String getExpectedNativeTypeFactoryClass() {
        return DoubleMixedJavaTimeKotlinNativeTypeFactory.class.getName();
    }

    @Override
    protected String getExpectedFEELLibClass() {
        return DoubleMixedJavaTimeFEELLib.class.getName();
    }

    @Override
    protected String getExpectedDecisionBaseClass() {
        return DoubleMixedJavaTimeDMNBaseDecision.class.getName();
    }
}