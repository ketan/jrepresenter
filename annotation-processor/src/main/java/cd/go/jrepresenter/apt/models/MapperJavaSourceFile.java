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

import com.squareup.javapoet.*;
import cd.go.jrepresenter.LinksMapper;
import cd.go.jrepresenter.LinksProvider;
import cd.go.jrepresenter.RequestContext;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapperJavaSourceFile {

    public final RepresenterAnnotation representerAnnotation;
    private final ClassToAnnotationMap context;

    public MapperJavaSourceFile(RepresenterAnnotation representerAnnotation, ClassToAnnotationMap context) {
        this.representerAnnotation = representerAnnotation;
        this.context = context;
    }

    public String toSource() {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(representerAnnotation.mapperClassImplSimpleName())
                .addModifiers(Modifier.PUBLIC);

        if (!representerAnnotation.shouldSkipSerialize()) {
            classBuilder
                    .addMethod(toJsonMethod())
                    .addMethod(toJsonCollectionMethod());
        }

        if (!representerAnnotation.shouldSkipDeserialize()) {
            classBuilder
                    .addMethod(fromJsonMethod())
                    .addMethod(fromJsonCollectionMethod());
        }

        if (representerAnnotation.hasLinksProvider()) {
            classBuilder.addField(FieldSpec.builder(
                    ParameterizedTypeName.get(ClassName.get(LinksProvider.class), representerAnnotation.getModelClass()), "LINKS_PROVIDER", Modifier.STATIC, Modifier.PRIVATE)
                    .initializer(CodeBlock.builder().add("new $T()", representerAnnotation.getLinksBuilderClass()).build())
                    .build());
        }
        JavaFile javaFile = JavaFile.builder(representerAnnotation.packageNameRelocated(), classBuilder.build()).build();
        return javaFile.toString();
    }

    private MethodSpec toJsonCollectionMethod() {
        return MethodSpec.methodBuilder("toJSON")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(List.class), representerAnnotation.getModelClass()), "values")
                .addParameter(RequestContext.class, "requestContext")
                .returns(List.class)
                .addCode(
                        CodeBlock.builder()
                                .addStatement("return values.stream().map(eachItem -> $T.toJSON(eachItem, requestContext)).collect($T.toList())", representerAnnotation.mapperClassImplRelocated(), Collectors.class)
                                .build()
                )
                .build();

    }

    private MethodSpec fromJsonCollectionMethod() {
        ParameterizedTypeName listOfMaps = ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(Map.class));
        ParameterizedTypeName listOfModels = ParameterizedTypeName.get(ClassName.get(List.class), representerAnnotation.getModelClass());
        return MethodSpec.methodBuilder("fromJSON")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(listOfMaps, "jsonArray")
                .returns(listOfModels)
                .addCode(
                        CodeBlock.builder()
                                .addStatement("return jsonArray.stream().map(eachItem -> $T.fromJSON(eachItem)).collect($T.toList())", representerAnnotation.mapperClassImplRelocated(), Collectors.class)
                                .build()
                )
                .build();

    }

    private MethodSpec toJsonMethod() {
        return MethodSpec.methodBuilder("toJSON")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(representerAnnotation.getModelClass(), "value")
                .addParameter(RequestContext.class, "requestContext")
                .returns(Map.class)
                .addCode(
                        CodeBlock.builder()
                                .addStatement("$T json = new $T()", Map.class, LinkedHashMap.class)
                                .add(serializeInternal())
                                .addStatement("return json")
                                .build()
                )
                .build();

    }

    private MethodSpec fromJsonMethod() {
        return MethodSpec.methodBuilder("fromJSON")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Map.class, "json")
                .returns(representerAnnotation.getModelClass())
                .addCode(
                        CodeBlock.builder()
                                .addStatement("$T model = new $T()", representerAnnotation.getModelClass(), representerAnnotation.getModelClass())
                                .add(deserializeInternal())
                                .addStatement("return model")
                                .build()
                )
                .build();
    }

    private CodeBlock serializeInternal() {
        CodeBlock.Builder serializeInternalBuilder = CodeBlock.builder();

        if (representerAnnotation.hasLinksProvider()) {
            serializeInternalBuilder.addStatement("json.putAll($T.toJSON($N, $N, $N))", LinksMapper.class, "LINKS_PROVIDER", "value", "requestContext");
        }

        List<BaseAnnotation> nonEmbeddedAnnotations = context.getAnnotationsOn(representerAnnotation).stream().filter(baseAnnotation -> !baseAnnotation.isEmbedded()).collect(Collectors.toList());
        List<BaseAnnotation> embeddedAnnotations = context.getAnnotationsOn(representerAnnotation).stream().filter(BaseAnnotation::isEmbedded).collect(Collectors.toList());

        nonEmbeddedAnnotations.forEach(baseAnnotation -> {
            serializeInternalBuilder.add(baseAnnotation.getSerializeCodeBlock(context, "json"));
        });

        if (!embeddedAnnotations.isEmpty()) {
            serializeInternalBuilder.addStatement("$T $N = new $T()", Map.class, "embeddedMap", LinkedHashMap.class);

            embeddedAnnotations.forEach(baseAnnotation -> {
                serializeInternalBuilder.add(baseAnnotation.getSerializeCodeBlock(context, "embeddedMap"));
            });

            serializeInternalBuilder.addStatement("json.put($S, $N)", "_embedded", "embeddedMap");
        }

        return serializeInternalBuilder.build();
    }

    private CodeBlock deserializeInternal() {
        CodeBlock.Builder deserializeInternalBuilder = CodeBlock.builder();
        context.getAnnotationsOn(representerAnnotation).forEach(baseAnnotation -> {
            deserializeInternalBuilder.add(baseAnnotation.getDeserializeCodeBlock(context));
        });
        return deserializeInternalBuilder.build();
    }

}
