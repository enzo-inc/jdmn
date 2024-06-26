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
package com.gs.dmn.feel.lib.type.time;

public interface DurationLib<DATE, DURATION> {
    DURATION duration(String from);

    DURATION yearsAndMonthsDuration(Object from, Object to);

    Long years(DURATION duration);

    Long months(DURATION duration);

    Long days(DURATION duration);

    Long hours(DURATION duration);

    Long minutes(DURATION duration);

    Long seconds(DURATION duration);

    DURATION abs(DURATION duration);
}
