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
package com.gs.dmn.ast.dmndi;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gs.dmn.ast.DMNBaseElement;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "@kind")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "diagram", value = Diagram.class),
        @JsonSubTypes.Type(name = "shape", value = Shape.class),
        @JsonSubTypes.Type(name = "edge", value = Edge.class),
})
public abstract class DiagramElement extends DMNBaseElement {
    private Extension extension;
    private Style style;
    private Style sharedStyle;
    private String id;
    private final Map<QName, String> otherAttributes = new HashMap<>();

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension value) {
        this.extension = value;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style value) {
        this.style = value;
    }

    public Style getSharedStyle() {
        return sharedStyle;
    }

    public void setSharedStyle(Style value) {
        this.sharedStyle = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    public static class Extension extends DMNBaseElement {
        private List<Object> any;

        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<>();
            }
            return this.any;
        }
    }
}
