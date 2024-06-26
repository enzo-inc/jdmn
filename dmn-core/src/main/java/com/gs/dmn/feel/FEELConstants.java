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
package com.gs.dmn.feel;

import java.util.Arrays;
import java.util.List;

public class FEELConstants {
    public static final String DATE_LITERAL_FUNCTION_NAME = "date";
    public static final String TIME_LITERAL_FUNCTION_NAME = "time";
    public static final String DATE_AND_TIME_LITERAL_FUNCTION_NAME = "date and time";
    public static final String DURATION_LITERAL_FUNCTION_NAME = "duration";

    public static final List<String> DATE_TIME_LITERAL_NAMES = Arrays.asList(
            DATE_LITERAL_FUNCTION_NAME, TIME_LITERAL_FUNCTION_NAME, DATE_AND_TIME_LITERAL_FUNCTION_NAME, DURATION_LITERAL_FUNCTION_NAME
    );

    private FEELConstants() {
    }
}
