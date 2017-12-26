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
import com.squareup.javapoet.ParameterizedTypeName;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionAnnotationTest {

    @Test
    public void shouldGenerateCodeToSerialize() throws Exception {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation(ClassName.bestGuess("com.foo.representers.UserRepresenter"), ClassName.bestGuess("com.foo.User"), null, false, false);
        Attribute modelAttribute = new Attribute("usersInternal", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("com.foo.User")));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withRepresenterClassName(ClassName.bestGuess("com.foo.representers.UserRepresenter"))
                .build();
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getSerializeCodeBlock(context, "json");

        String expectedCode = "json.put(\"users\", gen.com.foo.representers.UserMapper.toJSON(value.getUsersInternal(), requestContext));\n";
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToDeserialize() {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation(ClassName.bestGuess("com.foo.representers.UserRepresenter"), ClassName.bestGuess("com.foo.User"), null, false, false);
        Attribute modelAttribute = new Attribute("usersInternal", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("com.foo.User")));
        Attribute jsonAttribute = new Attribute("users", ParameterizedTypeName.get(List.class, Map.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withRepresenterClassName(ClassName.bestGuess("com.foo.representers.UserRepresenter"))
                .build();

        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.doGetDeserializeCodeBlock(context);

        String expectedCode = "" +
                "if (json.containsKey(\"users\")) {\n" +
                "  model.setUsersInternal(gen.com.foo.representers.UserMapper.fromJSON((java.util.List<java.util.Map>) json.get(\"users\")));\n" +
                "}\n";
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines(expectedCode);
    }

    @Test
    @Ignore
    public void shouldGenerateCodeToSerializeWithTargetWithSerializer() throws Exception {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation(ClassName.bestGuess("com.foo.representers.UserRepresenter"), ClassName.bestGuess("com.foo.User"), null, false, false);
        Attribute modelAttribute = new Attribute("usersInternal", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("com.foo.User")));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSerializerClassName(ClassName.bestGuess("com.foo.serializers.UserSerializer"))
                .build();

        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getSerializeCodeBlock(context, "json");

        String expectedCode = "json.put(\"users\", com.foo.representers.gen.UserMapper.toJSON((java.util.List) value.getUsersInternal(), requestContext));\n";
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithGetter() throws Exception {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation(ClassName.bestGuess("com.foo.representers.UserRepresenter"), ClassName.bestGuess("com.foo.User"), null, false, false);
        Attribute modelAttribute = new Attribute("usersInternal", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("com.foo.User")));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withGetterClassName(ClassName.bestGuess("com.foo.UsersGetter"))
                .build();

        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getSerializeCodeBlock(context, "json");

        String expectedCode = "json.put(\"users\", new com.foo.UsersGetter().apply(value));\n";
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithGetterAndSerializer() throws Exception {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation(ClassName.bestGuess("com.foo.representers.UserRepresenter"), ClassName.bestGuess("com.foo.User"), null, false, false);
        Attribute modelAttribute = new Attribute("usersInternal", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("com.foo.User")));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .build();

        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getSerializeCodeBlock(context, "json");

        String expectedCode = "json.put(\"users\", value.getUsersInternal());\n";
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines(expectedCode);
    }
}
