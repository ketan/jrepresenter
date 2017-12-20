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

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

public class PropertyAnnotation extends BaseAnnotation {
    public PropertyAnnotation(Attribute modelAttribute, Attribute jsonAttribute,
                              TypeName serializerClassName, TypeName deserializerClassName, TypeName representerClassName,
                              TypeName getterClassName, TypeName setterClassName, TypeName skipParse, TypeName skipRender) {
        super(modelAttribute, jsonAttribute, representerClassName, serializerClassName, deserializerClassName,
                getterClassName, setterClassName, skipParse, skipRender);
    }

    @Override
    protected CodeBlock doSetSerializeCodeBlock(ClassToAnnotationMap classToAnnotationMap, String jsonVariableName) {
        return putInJson(jsonVariableName,
                applyRenderRepresenter(classToAnnotationMap,
                        applySerializer(
                                applyGetter())));
    }

    private CodeBlock applySerializer(CodeBlock valueFromGetter) {
        if (hasSerializer()) {
            CodeBlock.Builder builder = CodeBlock.builder();
            builder.add("new $T().apply(", serializerClassName)
                    .add(valueFromGetter)
                    .add(")");
            return builder.build();
        } else {
            return valueFromGetter;
        }
    }

    @Override
    public CodeBlock doGetDeserializeCodeBlock(ClassToAnnotationMap context) {
        CodeBlock deserializeCodeBlock = applySetter(
                applyParseRepresenter(context,
                        applyDeserializer(
                                getValueFromJson())));
        return CodeBlock.builder()
                .beginControlFlow("if (json.containsKey($S))", jsonAttribute.nameAsSnakeCase())
                .add("$[")
                .add(deserializeCodeBlock)
                .add(";\n$]")
                .endControlFlow()
                .build();
    }

    private CodeBlock getValueFromJson() {
        return CodeBlock.builder()
                .add("($T) json.get($S)", jsonAttribute.type, jsonAttribute.nameAsSnakeCase())
                .build();
    }

    private CodeBlock applyDeserializer(CodeBlock valueFromJson) {
        if (hasDeserializer()) {
            return CodeBlock.builder()
                    .add("new $T().apply(", deserializerClassName)
                    .add(valueFromJson)
                    .add(")")
                    .build();
        } else {
            return valueFromJson;
        }
    }
}
