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

import cd.go.jrepresenter.EmptyLinksProvider;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyAnnotationTest {

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithoutSerializer() throws Exception {
        Attribute modelAttribute = new Attribute("fname", ClassName.get(String.class));
        Attribute jsonAttribute = new Attribute("firstName", ClassName.get(String.class));

        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .build();
        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("json.put(\"first_name\", value.getFname());\n");
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithSerializer() throws Exception {
        Attribute modelAttribute = new Attribute("fname", ClassName.bestGuess("com.tw.CaseInsensitiveString"));
        Attribute jsonAttribute = new Attribute("firstName", ClassName.get(String.class));
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSerializerClassName(ClassName.bestGuess("com.tw.CaseInsensitiveStringSerializer"))
                .build();
        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("json.put(\"first_name\", new com.tw.CaseInsensitiveStringSerializer().apply(value.getFname()));\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithoutSerializer() {
        Attribute modelAttribute = new Attribute("fname", ClassName.get(String.class));
        Attribute jsonAttribute = new Attribute("firstName", ClassName.get(String.class));
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .build();
        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("" +
                "if (json.containsKey(\"first_name\")) {\n" +
                "  model.setFname((java.lang.String) json.get(\"first_name\"));\n" +
                "}\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithoutSerializerWithPrimitiveType() {
        Attribute modelAttribute = new Attribute("age", TypeName.get(int.class));
        Attribute jsonAttribute = new Attribute("age", TypeName.get(int.class));
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .build();
        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("" +
                "if (json.containsKey(\"age\")) {\n" +
                "  model.setAge((int) json.get(\"age\"));\n" +
                "}\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithDeserializerClass() {
        Attribute modelAttribute = new Attribute("fname", ClassName.bestGuess("com.tw.CaseInsensitiveString"));
        Attribute jsonAttribute = new Attribute("firstName", TypeName.get(String.class));
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withDeserializerClassName(ClassName.bestGuess("com.tw.CaseInsensitiveStringDeserializer"))
                .build();

        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("" +
                "if (json.containsKey(\"first_name\")) {\n" +
                "  model.setFname(new com.tw.CaseInsensitiveStringDeserializer().apply((java.lang.String) json.get(\"first_name\")));\n" +
                "}\n");
    }

    @Test
    public void shouldGenerateCodeToSerializePropertyUsingRepresenter() {
        Attribute modelAttribute = new Attribute("triggeredBy", ClassName.bestGuess("com.tw.User"));
        Attribute jsonAttribute = new Attribute("user", null);
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation(ClassName.bestGuess("com.tw.UserRepresenter"), ClassName.bestGuess("com.tw.User"), ClassName.bestGuess(EmptyLinksProvider.class.getName()), false, false);
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);

        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withRepresenterClassName(ClassName.bestGuess("com.tw.UserRepresenter"))
                .build();

        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(context, "json");
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("json.put(\"user\", com.tw.gen.UserMapper.toJSON(value.getTriggeredBy(), requestContext));\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializePropertyUsingRepresenter() {
        Attribute modelAttribute = new Attribute("triggeredBy", ClassName.bestGuess("com.tw.User"));
        Attribute jsonAttribute = new Attribute("user", ClassName.get(Map.class));
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation(ClassName.bestGuess("com.tw.UserRepresenter"), ClassName.bestGuess("com.tw.User"), ClassName.bestGuess(EmptyLinksProvider.class.getName()), false, false);
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);

        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withRepresenterClassName(ClassName.bestGuess("com.tw.UserRepresenter"))
                .build();

        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(context);
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("" +
                "if (json.containsKey(\"user\")) {\n" +
                "  model.setTriggeredBy(com.tw.gen.UserMapper.fromJSON((java.util.Map) json.get(\"user\")));\n" +
                "}\n"
        );
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithGetter() throws Exception {
        Attribute modelAttribute = new Attribute("fname", ClassName.get(String.class));
        Attribute jsonAttribute = new Attribute("firstName", ClassName.get(String.class));
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withGetterClassName(ClassName.bestGuess("com.tw.FNameGetter"))
                .build();

        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("json.put(\"first_name\", new com.tw.FNameGetter().apply(value));\n");
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithGetterAndSerializer() throws Exception {
        Attribute modelAttribute = new Attribute("fname", ClassName.get(String.class));
        Attribute jsonAttribute = new Attribute("firstName", ClassName.get(String.class));
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSerializerClassName(ClassName.bestGuess("com.tw.CaseInsensitiveStringSerializer"))
                .withGetterClassName(ClassName.bestGuess("com.tw.FNameGetter"))
                .build();

        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualTo("json.put(\"first_name\", new com.tw.CaseInsensitiveStringSerializer().apply(new com.tw.FNameGetter().apply(value)));\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithSetter() {
        Attribute modelAttribute = new Attribute("triggeredBy", ClassName.bestGuess("com.tw.User"));
        Attribute jsonAttribute = new Attribute("user", ClassName.get(String.class));
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSetterClassName(ClassName.bestGuess("com.tw.TriggeredBySetter"))
                .build();

        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("" +
                "if (json.containsKey(\"user\")) {\n" +
                "  new com.tw.TriggeredBySetter().accept(model, (java.lang.String) json.get(\"user\"));\n" +
                "}\n"
        );
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithSetterAndDeserializer() {
        Attribute modelAttribute = new Attribute("triggeredBy", ClassName.bestGuess("com.tw.User"));
        Attribute jsonAttribute = new Attribute("user", ClassName.get(String.class));
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withDeserializerClassName(ClassName.bestGuess("com.tw.CaseInsensitiveStringDeserializer"))
                .withSetterClassName(ClassName.bestGuess("com.tw.TriggeredBySetter"))
                .build();

        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("" +
                "if (json.containsKey(\"user\")) {\n" +
                "  new com.tw.TriggeredBySetter().accept(model, new com.tw.CaseInsensitiveStringDeserializer().apply((java.lang.String) json.get(\"user\")));\n" +
                "}\n"
        );
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithSetterAndRepresenter() {

    }


}
