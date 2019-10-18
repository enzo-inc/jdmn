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

import java.util.*;

public class FEELTypes {
    public static final Map<String, Type> FEEL_NAME_TO_FEEL_TYPE = new LinkedHashMap<>();
    static {
        FEEL_NAME_TO_FEEL_TYPE.put(AnyType.ANY.getName(), AnyType.ANY);
        FEEL_NAME_TO_FEEL_TYPE.put(NumberType.NUMBER.getName(), NumberType.NUMBER);
        FEEL_NAME_TO_FEEL_TYPE.put(BooleanType.BOOLEAN.getName(), BooleanType.BOOLEAN);
        FEEL_NAME_TO_FEEL_TYPE.put(StringType.STRING.getName(), StringType.STRING);
        FEEL_NAME_TO_FEEL_TYPE.put(EnumerationType.ENUMERATION.getName(), StringType.STRING);
        FEEL_NAME_TO_FEEL_TYPE.put(DateType.DATE.getName(), DateType.DATE);
        FEEL_NAME_TO_FEEL_TYPE.put(TimeType.TIME.getName(), TimeType.TIME);
        FEEL_NAME_TO_FEEL_TYPE.put(DateTimeType.DATE_TIME_CAMEL.getName(), DateTimeType.DATE_AND_TIME);
        FEEL_NAME_TO_FEEL_TYPE.put(DateTimeType.DATE_TIME.getName(), DateTimeType.DATE_AND_TIME);
        FEEL_NAME_TO_FEEL_TYPE.put(DateTimeType.DATE_AND_TIME.getName(), DateTimeType.DATE_AND_TIME);
        FEEL_NAME_TO_FEEL_TYPE.put(DurationType.DAYS_AND_TIME_DURATION.getName(), DurationType.DAYS_AND_TIME_DURATION);
        FEEL_NAME_TO_FEEL_TYPE.put(DurationType.DAY_TIME_DURATION.getName(), DurationType.DAYS_AND_TIME_DURATION);
        FEEL_NAME_TO_FEEL_TYPE.put(DurationType.YEARS_AND_MONTHS_DURATION.getName(), DurationType.YEARS_AND_MONTHS_DURATION);
        FEEL_NAME_TO_FEEL_TYPE.put(DurationType.YEAR_MONTH_DURATION.getName(), DurationType.YEARS_AND_MONTHS_DURATION);
    };

    public static final List<String> FEEL_TYPE_NAMES = Arrays.asList(
            AnyType.ANY.getName(), NumberType.NUMBER.getName(), BooleanType.BOOLEAN.getName(), StringType.STRING.getName(),
            DateType.DATE.getName(), TimeType.TIME.getName(), DateTimeType.DATE_TIME_CAMEL.getName(), DateTimeType.DATE_TIME.getName(), DateTimeType.DATE_AND_TIME.getName(),
            DurationType.DAYS_AND_TIME_DURATION.getName(), DurationType.YEARS_AND_MONTHS_DURATION.getName(), EnumerationType.ENUMERATION.getName());

    public static final List<Type> FEEL_PRIMITIVE_TYPES = Arrays.asList(new Type[]{
            AnyType.ANY,
            NumberType.NUMBER,
            BooleanType.BOOLEAN,
            StringType.STRING,
            StringType.STRING,
            DateType.DATE,
            TimeType.TIME,
            DateTimeType.DATE_AND_TIME,
            DurationType.DAYS_AND_TIME_DURATION,
            DurationType.YEARS_AND_MONTHS_DURATION
    });

    public static final List<String> FEEL_LITERAL_DATE_TIME_NAMES = Arrays.asList(
            NumberType.NUMBER.getConversionFunction(),
            DateType.DATE.getConversionFunction(),
            TimeType.TIME.getConversionFunction(),
            DateTimeType.DATE_AND_TIME.getConversionFunction(),
            DurationType.DAYS_AND_TIME_DURATION.getConversionFunction(),
            DurationType.YEARS_AND_MONTHS_DURATION.getConversionFunction());

    public static final Map<Type, String> FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION = new LinkedHashMap<>();
    static {
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(NumberType.NUMBER, "number");
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(BooleanType.BOOLEAN, null);
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(StringType.STRING, null);
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(DateType.DATE, "date");
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(TimeType.TIME, "time");
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(DateTimeType.DATE_TIME, "dateAndTime");
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(DateTimeType.DATE_AND_TIME, "dateAndTime");
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(DateTimeType.DATE_TIME_CAMEL, "dateAndTime");
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(DurationType.DAYS_AND_TIME_DURATION, "duration");
        FEEL_PRIMITIVE_TYPE_TO_JAVA_CONVERSION_FUNCTION.put(DurationType.YEARS_AND_MONTHS_DURATION, "duration");
    };
}
