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
package com.gs.dmn.signavio.feel.lib.type.string;

import com.gs.dmn.feel.lib.type.string.DefaultStringType;

public class DefaultSignavioStringType extends DefaultStringType {
    public DefaultSignavioStringType() {
    }

    @Override
    public Boolean stringLessThan(String first, String second) {
        return null;
    }

    @Override
    public Boolean stringGreaterThan(String first, String second) {
        return null;
    }

    @Override
    public Boolean stringLessEqualThan(String first, String second) {
        return null;
    }

    @Override
    public Boolean stringGreaterEqualThan(String first, String second) {
        return null;
    }
}
