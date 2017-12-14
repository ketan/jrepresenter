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

import cd.go.jrepresenter.apt.models.Attribute;
import cd.go.jrepresenter.apt.models.PropertyAnnotation;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyAnnotationTest {

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithoutSerializer() throws Exception {
        Attribute modelAttribute = new Attribute("fname", ClassName.get(String.class));
        Attribute jsonAttribute = new Attribute("firstName", ClassName.get(String.class));
        PropertyAnnotation propertyAnnotation = new PropertyAnnotation(modelAttribute, jsonAttribute, null, null);
        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("json.put(\"first_name\", value.getFname());\n");
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithSerializer() throws Exception {
        Attribute modelAttribute = new Attribute("fname", ClassName.bestGuess("com.tw.CaseInsensitiveString"));
        Attribute jsonAttribute = new Attribute("firstName", ClassName.get(String.class));
        PropertyAnnotation propertyAnnotation = new PropertyAnnotation(modelAttribute, jsonAttribute, ClassName.bestGuess("com.tw.CaseInsensitiveStringSerializer"), null);
        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("json.put(\"first_name\", new com.tw.CaseInsensitiveStringSerializer().apply(value.getFname()));\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithoutSerializer() {
        Attribute modelAttribute = new Attribute("fname", ClassName.get(String.class));
        Attribute jsonAttribute = new Attribute("firstName", ClassName.get(String.class));
        PropertyAnnotation propertyAnnotation = new PropertyAnnotation(modelAttribute, jsonAttribute, null, null);
        CodeBlock codeBlock = propertyAnnotation.getDeserializeCodeBlock(null);
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("" +
                "if (json.containsKey(\"first_name\")) {\n" +
                "  model.setFname((java.lang.String) json.get(\"first_name\"));\n" +
                "}\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithoutSerializerWithPrimitiveType() {
        Attribute modelAttribute = new Attribute("age", TypeName.get(int.class));
        Attribute jsonAttribute = new Attribute("age", TypeName.get(int.class));
        PropertyAnnotation propertyAnnotation = new PropertyAnnotation(modelAttribute, jsonAttribute, null, null);
        CodeBlock codeBlock = propertyAnnotation.getDeserializeCodeBlock(null);
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("" +
                "if (json.containsKey(\"age\")) {\n" +
                "  model.setAge((int) json.get(\"age\"));\n" +
                "}\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithDeserializerClass() {
        Attribute modelAttribute = new Attribute("fname", ClassName.bestGuess("com.tw.CaseInsensitiveString"));
        Attribute jsonAttribute = new Attribute("firstName", TypeName.get(String.class));
        PropertyAnnotation propertyAnnotation = new PropertyAnnotation(modelAttribute, jsonAttribute, null, ClassName.bestGuess("com.tw.CaseInsensitiveStringDeserializer"));
        CodeBlock codeBlock = propertyAnnotation.getDeserializeCodeBlock(null);
        assertThat(codeBlock.toString()).isEqualToNormalizingNewlines("" +
                "if (json.containsKey(\"first_name\")) {\n" +
                "  model.setFname(new com.tw.CaseInsensitiveStringDeserializer().apply((java.lang.String) json.get(\"first_name\")));\n" +
                "}\n");
    }
}
