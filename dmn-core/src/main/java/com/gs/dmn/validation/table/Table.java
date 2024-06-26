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
package com.gs.dmn.validation.table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table {
    private final List<Input> inputs = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();

    public Table(List<Input> inputs, List<Rule> rules) {
        if (inputs != null) {
            this.inputs.addAll(inputs);
        }
        if (rules != null) {
            this.rules.addAll(rules);
        }
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public Input getInput(int columnIndex) {
        return inputs.get(columnIndex);
    }

    public List<Rule> getRules() {
        return rules;
    }

    public boolean isEmpty() {
        return inputs.isEmpty() || rules.isEmpty();
    }

    public Bound getLowerBoundForColumn(int columnIndex) {
        List<Rule> rules = this.getRules();
        Bound min = null;
        for (Rule rule: rules) {
            Interval interval = rule.getInterval(columnIndex);
            if (interval instanceof NumericInterval) {
                Bound lowerBound = interval.getLowerBound();
                if (min == null) {
                    min = lowerBound;
                } else if (Bound.COMPARATOR.compare(lowerBound, min) < 0) {
                    min = lowerBound;
                }
            } else if (interval instanceof EnumerationInterval) {
                return new Bound(interval, true, true, Bound.ZERO);
            }
        }
        return min;
    }

    public Bound getUpperBoundForColumn(int columnIndex) {
        List<Rule> rules = this.getRules();
        Bound max = null;
        for (Rule rule: rules) {
            Interval interval = rule.getInterval(columnIndex);
            if (interval instanceof NumericInterval) {
                Bound upperBound = interval.getUpperBound();
                if (max == null) {
                    max = upperBound;
                } else if (Bound.COMPARATOR.compare(upperBound, max) > 0) {
                    max = upperBound;
                }
            } else if (interval instanceof EnumerationInterval) {
                Input input = this.getInput(columnIndex);
                Double size = (double) input.getAllowedValues().size();
                return new Bound(interval, false, false, size);
            }
        }
        return max;
    }

    @Override
    public String toString() {
        return String.format("[%s]", rules.stream().map(i -> i == null ? null : i.toString()).collect(Collectors.joining(", ")));
    }
}
