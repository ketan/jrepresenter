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
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionAnnotation extends BaseAnnotation {

    private static final ParameterizedTypeName LIST_OF_MAPS_TYPE = ParameterizedTypeName.get(List.class, Map.class);

    public CollectionAnnotation(Attribute modelAttribute, Attribute jsonAttribute, TypeName representerClassName, TypeName serializerClassName, TypeName deserializerClassName, TypeName getterClassName, TypeName setterClassName, TypeName skipParse, TypeName skipRender) {
        super(modelAttribute, jsonAttribute, representerClassName, serializerClassName, deserializerClassName, getterClassName, setterClassName, skipParse, skipRender);
    }

    @Override
    protected CodeBlock doSetSerializeCodeBlock(ClassToAnnotationMap context, String jsonVariableName) {
        return putInJson(jsonVariableName,
                applyRenderRepresenter(context,
                        applySerializer(
                                applyGetter())));
    }

    @Override
    public CodeBlock doGetDeserializeCodeBlock(ClassToAnnotationMap classToAnnotationMap) {
        CodeBlock deserializeCodeBlock = applySetter(
                applyParseRepresenter(classToAnnotationMap,
                        applyDeserializer(
                                getValueFromJson())));

        return CodeBlock.builder()
                .beginControlFlow("if (json.containsKey($S))", jsonAttribute.name)
                .add("$[")
                .add(deserializeCodeBlock)
                .add(";\n$]")
                .endControlFlow()
                .build();
    }

    private CodeBlock applySerializer(CodeBlock valueFromGetter) {
        if (hasSerializer()) {
            return CodeBlock.builder()
                    .add("(")
                    .add(valueFromGetter)
                    .add(")")
                    .add(".stream().map(new $T()::apply).collect($T.toList())",
                            serializerClassName,
                            Collectors.class)
                    .build();
        } else {
            return valueFromGetter;
        }
    }

    private CodeBlock applyDeserializer(CodeBlock valueFromJson) {
        if (hasDeserializer()) {
            return CodeBlock.builder()
                    .add("(")
                    .add(valueFromJson)
                    .add(")")
                    .add(".stream().map(new $T()::apply).collect($T.toList())", deserializerClassName, Collectors.class)
                    .build();
        } else {
            return valueFromJson;
        }
    }

    private CodeBlock getValueFromJson() {
        return CodeBlock.builder()
                .add("($T) json.get($S)", jsonAttribute.type, jsonAttribute.nameAsSnakeCase())
                .build();
    }
}
