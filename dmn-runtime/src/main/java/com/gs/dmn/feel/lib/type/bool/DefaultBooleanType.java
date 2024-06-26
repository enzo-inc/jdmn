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
package com.gs.dmn.feel.lib.type.bool;

import com.gs.dmn.feel.lib.type.BaseType;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DefaultBooleanType extends BaseType implements BooleanType {
    @Override
    public boolean isBoolean(Object value) {
        return value instanceof Boolean;
    }

    @Override
    public Boolean booleanValue(Boolean value) {
        return value;
    }

    @Override
    public Boolean booleanNot(Object operand) {
        return TernaryBooleanLogicUtil.getInstance().not(operand);
    }

    @Override
    public Boolean booleanOr(List<Object> operands) {
        if (operands == null || operands.size() < 2) {
            return null;
        } else {
            Object result = operands.get(0);
            for (int i = 1; i < operands.size(); i++) {
                result = binaryBooleanOr(result, operands.get(i));
            }
            return (Boolean) result;
        }
    }

    @Override
    public Boolean booleanOr(Object... operands) {
        if (operands == null || operands.length < 2) {
            return null;
        } else {
            Object result = operands[0];
            for (int i = 1; i < operands.length; i++) {
                result = binaryBooleanOr(result, operands[i]);
            }
            return (Boolean) result;
        }
    }

    @Override
    public Boolean binaryBooleanOr(Object first, Object second) {
        return TernaryBooleanLogicUtil.getInstance().or(first, second);
    }

    @Override
    public Boolean booleanAnd(List<Object> operands) {
        if (operands == null || operands.size() < 2) {
            return null;
        } else {
            Object result = operands.get(0);
            for (int i = 1; i < operands.size(); i++) {
                result = binaryBooleanAnd(result, operands.get(i));
            }
            return (Boolean) result;
        }
    }

    @Override
    public Boolean booleanAnd(Object... operands) {
        if (operands == null || operands.length < 2) {
            return null;
        } else {
            Object result = operands[0];
            for (int i = 1; i < operands.length; i++) {
                result = binaryBooleanAnd(result, operands[i]);
            }
            return (Boolean) result;
        }
    }

    @Override
    public Boolean binaryBooleanAnd(Object first, Object second) {
        return TernaryBooleanLogicUtil.getInstance().and(first, second);
    }

    @Override
    public Boolean booleanIs(Boolean first, Boolean second) {
        return booleanEqual(first, second);
    }

    @Override
    public Boolean booleanEqual(Boolean first, Boolean second) {
        if (first == null && second == null) {
            return TRUE;
        } else if (first == null) {
            return FALSE;
        } else if (second == null) {
            return FALSE;
        } else {
            return first.equals(second);
        }
    }

    @Override
    public Boolean booleanNotEqual(Boolean first, Boolean second) {
        return booleanNot(booleanEqual(first, second));
    }
}
