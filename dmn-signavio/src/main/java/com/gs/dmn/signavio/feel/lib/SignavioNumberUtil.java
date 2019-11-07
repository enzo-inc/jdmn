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
package com.gs.dmn.signavio.feel.lib;

import java.math.BigDecimal;

public class SignavioNumberUtil {
    public static BigDecimal round(BigDecimal number, BigDecimal digits) {
        if (number == null || digits == null) {
            return null;
        }

        return number.setScale(digits.intValue(), BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal roundDown(BigDecimal number, BigDecimal digits) {
        if (number == null || digits == null) {
            return null;
        }

        return number.setScale(digits.intValue(), BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal roundUp(BigDecimal number, BigDecimal digits) {
        if (number == null || digits == null) {
            return null;
        }

        return number.setScale(digits.intValue(), BigDecimal.ROUND_UP);
    }
}
