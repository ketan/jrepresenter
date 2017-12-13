/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.jrepresenter.apt.models;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import cd.go.jrepresenter.annotations.Property;

public class PropertyAnnotation extends BaseAnnotation {

    private static final ClassName NULL_FUNCTION = ClassName.get(Property.NullFunction.class);

    private final TypeName serializerClassName;
    private final TypeName deserializerClassName;

    public PropertyAnnotation(Attribute modelAttribute, Attribute jsonAttribute,
                              TypeName serializerClassName, TypeName deserializerClassName) {
        super(modelAttribute, jsonAttribute);
        this.serializerClassName = serializerClassName == null ? NULL_FUNCTION : serializerClassName;
        this.deserializerClassName = deserializerClassName == null ? NULL_FUNCTION : deserializerClassName;
    }

    @Override
    public CodeBlock getSerializeCodeBlock(ClassToAnnotationMap classToAnnotationMap, String jsonVariableName) {
        return CodeBlock.builder()
                .add(serializeStatement(jsonVariableName))
                .build();
    }

    private CodeBlock serializeStatement(String jsonVariableName) {
        if (serializerClassName.equals(NULL_FUNCTION)) {
            return CodeBlock.builder()
                    .addStatement("$N.put($S, value.$N())", jsonVariableName, jsonAttribute.name, modelAttributeGetter())
                    .build();
        } else {
            return CodeBlock.builder()
                    .addStatement("$N.put($S, new $T().apply(value.$N()))", jsonVariableName, jsonAttribute.name, serializerClassName, modelAttributeGetter())
                    .build();
        }
    }

    @Override
    public CodeBlock getDeserializeCodeBlock(ClassToAnnotationMap classToAnnotationMap) {
        return CodeBlock.builder()
                .beginControlFlow("if (json.containsKey($S))", jsonAttribute.name)
                .add(deserializeStatement())

                .endControlFlow()
                .build();
    }

    private CodeBlock deserializeStatement() {
        if (deserializerClassName.equals(NULL_FUNCTION)) {
            return CodeBlock.builder()
                    .addStatement("model.$N(($T) json.get($S))", modelAttributeSetter(), modelAttribute.type, jsonAttribute.name)
                    .build();
        } else {
            return CodeBlock.builder()
                    .addStatement("model.$N(new $T().apply(($T) json.get($S)))", modelAttributeSetter(), deserializerClassName, jsonAttribute.type, jsonAttribute.name)
                    .build();
        }
    }
}
