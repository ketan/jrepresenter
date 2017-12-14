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

package cd.go.jrepresenter.apt.processor;

import cd.go.jrepresenter.apt.models.ClassToAnnotationMap;
import cd.go.jrepresenter.apt.models.RepresenterAnnotation;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import cd.go.jrepresenter.apt.models.Attribute;
import cd.go.jrepresenter.apt.models.CollectionAnnotation;
import cd.go.jrepresenter.EmptyLinksProvider;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionAnnotationTest {

    @Test
    public void shouldGenerateCodeToSerialize() throws Exception {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation("com.foo.UserRepresenter", "com.foo.User", EmptyLinksProvider.class.getName());
        Attribute modelAttribute = new Attribute("usersInternal", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("com.foo.User")));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = new CollectionAnnotation("com.foo.UserRepresenter", modelAttribute, jsonAttribute);
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getSerializeCodeBlock(context, "embeddedMap");

        String expectedCode = "embeddedMap.put(\"users\", com.foo.gen.UserMapper.toJSON((java.util.List) value.getUsersInternal(), requestContext));\n";
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToDeserialize() {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation("com.foo.UserRepresenter", "com.foo.User", EmptyLinksProvider.class.getName());
        Attribute modelAttribute = new Attribute("usersInternal", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("com.foo.User")));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = new CollectionAnnotation("com.foo.UserRepresenter", modelAttribute, jsonAttribute);
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getDeserializeCodeBlock(context);

        String expectedCode = "" +
                "if (json.containsKey(\"users\")) {\n" +
                "  model.setUsersInternal(com.foo.gen.UserMapper.fromJSON((java.util.List<java.util.Map>) json.get(\"users\")));\n" +
                "}\n";
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines(expectedCode);
    }


}
