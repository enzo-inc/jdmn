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
package com.gs.dmn.feel.synthesis.type;

import com.gs.dmn.DMNModelRepository;
import com.gs.dmn.feel.analysis.semantics.type.*;
import com.gs.dmn.runtime.DMNRuntimeException;
import com.gs.dmn.runtime.Pair;
import com.gs.dmn.transformation.basic.BasicDMN2JavaTransformer;
import com.gs.dmn.transformation.basic.BasicDMNToNativeTransformer;
import com.gs.dmn.transformation.proto.Field;
import com.gs.dmn.transformation.proto.FieldType;
import com.gs.dmn.transformation.proto.MessageType;
import com.gs.dmn.transformation.proto.Service;
import org.apache.commons.lang3.StringUtils;
import org.omg.spec.dmn._20180521.model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.gs.dmn.feel.analysis.semantics.type.AnyType.ANY;
import static com.gs.dmn.feel.analysis.semantics.type.BooleanType.BOOLEAN;
import static com.gs.dmn.feel.analysis.semantics.type.DateTimeType.DATE_AND_TIME;
import static com.gs.dmn.feel.analysis.semantics.type.DateType.DATE;
import static com.gs.dmn.feel.analysis.semantics.type.DurationType.DAYS_AND_TIME_DURATION;
import static com.gs.dmn.feel.analysis.semantics.type.DurationType.YEARS_AND_MONTHS_DURATION;
import static com.gs.dmn.feel.analysis.semantics.type.EnumerationType.ENUMERATION;
import static com.gs.dmn.feel.analysis.semantics.type.NumberType.NUMBER;
import static com.gs.dmn.feel.analysis.semantics.type.StringType.STRING;
import static com.gs.dmn.feel.analysis.semantics.type.TimeType.TIME;

public class ProtoBufferFactory {
    private static final Map<String, String> TIME_FEEL_TO_PROTO_TYPE = new LinkedHashMap<>();
    public static final String OPTIONAL = "optional";
    public static final String REPEATED = "repeated";

    static {
        TIME_FEEL_TO_PROTO_TYPE.put(ENUMERATION.getName(), "string");
        TIME_FEEL_TO_PROTO_TYPE.put(YEARS_AND_MONTHS_DURATION.getName(), null);
        TIME_FEEL_TO_PROTO_TYPE.put(DAYS_AND_TIME_DURATION.getName(), null);
        TIME_FEEL_TO_PROTO_TYPE.put(DATE_AND_TIME.getName(), null);
        TIME_FEEL_TO_PROTO_TYPE.put(TIME.getName(), null);
        TIME_FEEL_TO_PROTO_TYPE.put(DATE.getName(), null);
        TIME_FEEL_TO_PROTO_TYPE.put(STRING.getName(), "string");
        TIME_FEEL_TO_PROTO_TYPE.put(BOOLEAN.getName(), "bool");
        TIME_FEEL_TO_PROTO_TYPE.put(NUMBER.getName(), "double");
        TIME_FEEL_TO_PROTO_TYPE.put(ANY.getName(), null);
    }

    private final BasicDMNToNativeTransformer transformer;
    private final DMNModelRepository repository;

    public ProtoBufferFactory(BasicDMN2JavaTransformer transformer) {
        this.transformer = transformer;
        this.repository = transformer.getDMNModelRepository();
    }

    public Pair<Pair<List<MessageType>, List<MessageType>>, List<Service>> dmnToProto(TDefinitions definitions) {
        if (!(this.transformer.isGenerateProtoMessages() || this.transformer.isGenerateProtoServices())) {
            return null;
        }

        // Make messages for complex types
        List<MessageType> dataTypes = itemDefinitionsToMessageTypes(definitions);

        // Make Request and Response messages for every DRG Element
        List<MessageType> requestResponseTypes = drgElementsToMessageTypes(definitions);

        // Make services
        List<Service> services = new ArrayList<>();
        if (this.transformer.isGenerateProtoServices()) {
            services = drgElementsToServices(definitions);
        }

        return new Pair<>(new Pair<>(dataTypes, requestResponseTypes), services);
    }

    private List<MessageType> itemDefinitionsToMessageTypes(TDefinitions definitions) {
        List<MessageType> messageTypes = new ArrayList<>();
        List<TItemDefinition> itemDefinitions = this.repository.compositeItemDefinitions(definitions);
        for (TItemDefinition itemDefinition: itemDefinitions) {
            String messageName = drgElementName(itemDefinition);
            List<Field> fields = new ArrayList<>();
            for (TItemDefinition child: itemDefinition.getItemComponent()) {
                String fieldName = this.transformer.namedElementVariableName(child);
                FieldType fieldType = protoType(child);
                fields.add(new Field(fieldName, fieldType));
            }
            messageTypes.add(new MessageType(messageName, fields));
        }
        return messageTypes;
    }

    public List<MessageType> drgElementsToMessageTypes(TDefinitions definitions) {
        List<MessageType> messageTypes = new ArrayList<>();
        List<TDRGElement> drgElements = this.repository.findDRGElements(definitions);
        for (TDRGElement element: drgElements) {
            if (element instanceof TInputData) {
                continue;
            }

            // Request
            String messageName = drgElementName(element);
            String requestMessageName = requestMessageName(element);
            List<Field> requestFields = new ArrayList<>();
            List<Pair<String, Type>> parameters = this.transformer.drgElementTypeSignature(element, this.transformer::nativeName);
            for (Pair<String, Type> parameter: parameters) {
                String fieldName = parameter.getLeft();
                FieldType fieldType = toProtoType(parameter.getRight());
                requestFields.add(new Field(fieldName, fieldType));
            }
            messageTypes.add(new MessageType(requestMessageName, requestFields));

            // Response
            String responseMessageName = responseMessageName(element);
            List<Field> responseFields = new ArrayList<>();
            Type type = this.transformer.drgElementOutputFEELType(element);
            responseFields.add(new Field(responseFieldName(element), toProtoType(type)));
            messageTypes.add(new MessageType(responseMessageName, responseFields));
        }
        return messageTypes;
    }

    public List<Service> drgElementsToServices(TDefinitions definitions) {
        List<Service> services = new ArrayList<>();
        List<TDRGElement> drgElements = this.repository.findDRGElements(definitions);
        for (TDRGElement element: drgElements) {
            if (element instanceof TInputData) {
                continue;
            }

            // Add Service
            String serviceName = drgElementName(element) + "Service";
            services.add(new Service(serviceName, requestMessageName(element), responseMessageName(element)));
        }
        return services;
    }

    private String drgElementName(TNamedElement element) {
        return this.transformer.upperCaseFirst(element.getName());
    }

    private String responseFieldName(TDRGElement element) {
        return this.transformer.lowerCaseFirst(element.getName());
    }

    private String protoServiceName(TNamedElement element) {
        return drgElementName(element);
    }

    private String requestMessageName(TDRGElement element) {
        return protoServiceName(element) + "Request";
    }

    private String responseMessageName(TDRGElement element) {
        return drgElementName(element) + "Response";
    }

    private FieldType protoType(TItemDefinition itemDefinition) {
        Type type = this.transformer.toFEELType(itemDefinition);
        FieldType protoType = toProtoType(type);
        return protoType;
    }

    private FieldType toProtoType(Type type) {
        String modifier = OPTIONAL;
        if (type instanceof AnyType) {
            return new FieldType(modifier, "Any");
        } else if (type instanceof NamedType) {
            String typeName = ((NamedType) type).getName();
            if (StringUtils.isBlank(typeName)) {
                throw new DMNRuntimeException(String.format("Missing type name in '%s'", type));
            }
            String primitiveType = toNativeType(typeName);
            if (!StringUtils.isBlank(primitiveType)) {
                return new FieldType(modifier, primitiveType);
            } else {
                if (type instanceof ItemDefinitionType) {
                    String modelName = ((ItemDefinitionType) type).getModelName();
                    String javaPackage = this.transformer.nativeModelPackageName(modelName);
                    String protoPackage = this.transformer.protoPackage(javaPackage);
                    String qType = this.transformer.qualifiedName(protoPackage, this.transformer.upperCaseFirst(typeName));
                    return new FieldType(modifier, qType);
                } else {
                    throw new DMNRuntimeException(String.format("Cannot infer platform type for '%s'", type));
                }
            }
        }  else if (type instanceof ListType) {
            Type elementType = ((ListType) type).getElementType();
            if (elementType instanceof AnyType) {
                return new FieldType(REPEATED, "Any");
            } else {
                FieldType fieldType = toProtoType(elementType);
                return new FieldType(REPEATED, fieldType.getType());
            }
        }
        throw new IllegalArgumentException(String.format("Type '%s' is not supported yet", type));
    }

    private String toNativeType(String feelType) {
        return TIME_FEEL_TO_PROTO_TYPE.get(feelType);
    }
}
