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
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class RepresentsSubClassesAnnotationTest {

    private ClassName userRepresenterClass = ClassName.bestGuess("com.tw.representers.UserRepresenter");
    private ClassName guestUserRepresenterClass = ClassName.bestGuess("com.tw.representers.GuestUserRepresenter");
    private ClassName adminUserRepresenterClass = ClassName.bestGuess("com.tw.representers.AdminUserRepresenter");
    private ClassName userClass = ClassName.bestGuess("com.tw.User");
    private ClassName guestUserClass = ClassName.bestGuess("com.tw.GuestUser");
    private ClassName adminUserClass = ClassName.bestGuess("com.tw.AdminUser");
    private ClassToAnnotationMap context;
    private SubClassInfoAnnotation guestSubClassInfo;
    private SubClassInfoAnnotation adminSubClassInfo;
    private RepresenterAnnotation userRepresenterAnnotation;

    @Before
    public void setUp() throws Exception {
        context = new ClassToAnnotationMap();
        userRepresenterAnnotation = new RepresenterAnnotation(userRepresenterClass, userClass, null, false, false);
        context.add(userRepresenterAnnotation);
        context.add(new RepresenterAnnotation(guestUserRepresenterClass, guestUserClass, null, false, false));
        context.add(new RepresenterAnnotation(adminUserRepresenterClass, adminUserClass, null, false, false));
        guestSubClassInfo = new SubClassInfoAnnotation(guestUserRepresenterClass, "guest", null);
        adminSubClassInfo = new SubClassInfoAnnotation(adminUserRepresenterClass, "admin", null);
    }

    @Test
    public void shouldGetSerializeCodeBlockWhenNestedUnderAttribute() {
        RepresentsSubClassesAnnotation representsSubClassesAnnotation = new RepresentsSubClassesAnnotation("type",
                "attributes", Arrays.asList(guestSubClassInfo, adminSubClassInfo));

        CodeBlock serializeCodeBlock = representsSubClassesAnnotation.getSerializeCodeBlock(context);

        assertThat(serializeCodeBlock.toString()).isEqualToNormalizingNewlines("" +
                "java.util.Map subClassProperties = null;\n" +
                "if (value instanceof com.tw.GuestUser) {\n" +
                "  subClassProperties = com.tw.representers.gen.GuestUserMapper.toJSON((com.tw.GuestUser) value, requestContext);\n" +
                "}\n" +
                "else if (value instanceof com.tw.AdminUser) {\n" +
                "  subClassProperties = com.tw.representers.gen.AdminUserMapper.toJSON((com.tw.AdminUser) value, requestContext);\n" +
                "}\n" +
                "json.put(\"attributes\", subClassProperties);\n");
    }

    @Test
    public void shouldGetSerializeCodeBlockWhenNotNestedUnderAttribute() {
        RepresentsSubClassesAnnotation representsSubClassesAnnotation = new RepresentsSubClassesAnnotation("type",
                "", Arrays.asList(guestSubClassInfo, adminSubClassInfo));

        CodeBlock serializeCodeBlock = representsSubClassesAnnotation.getSerializeCodeBlock(context);

        assertThat(serializeCodeBlock.toString()).isEqualToNormalizingNewlines("" +
                "java.util.Map subClassProperties = null;\n" +
                "if (value instanceof com.tw.GuestUser) {\n" +
                "  subClassProperties = com.tw.representers.gen.GuestUserMapper.toJSON((com.tw.GuestUser) value, requestContext);\n" +
                "}\n" +
                "else if (value instanceof com.tw.AdminUser) {\n" +
                "  subClassProperties = com.tw.representers.gen.AdminUserMapper.toJSON((com.tw.AdminUser) value, requestContext);\n" +
                "}\n" +
                "json.putAll(subClassProperties);\n");
    }

    @Test
    public void shouldGetDeserializeCodeBlockWhenNestedUnderAttribute() {
        RepresentsSubClassesAnnotation representsSubClassesAnnotation = new RepresentsSubClassesAnnotation("type",
                "attributes", Arrays.asList(guestSubClassInfo, adminSubClassInfo));

        CodeBlock deserializeCodeBlock = representsSubClassesAnnotation.getDeserializeCodeBlock(context, userRepresenterAnnotation);

        assertThat(deserializeCodeBlock.toString()).isEqualToNormalizingNewlines("" +
                "com.tw.User model = null;\n" +
                "java.lang.String type = (java.lang.String) json.get(\"type\");\n" +
                "if (type.equals(\"guest\")) {\n" +
                "  model = com.tw.representers.gen.GuestUserMapper.fromJSON((java.util.Map) json.get(\"attributes\"));\n" +
                "}\n" +
                "else if (type.equals(\"admin\")) {\n" +
                "  model = com.tw.representers.gen.AdminUserMapper.fromJSON((java.util.Map) json.get(\"attributes\"));\n" +
                "}\n" +
                "else {\n" +
                "  throw new java.lang.RuntimeException(\"Could not find any subclass for specified type. Possible values are: guest,admin\");\n" +
                "}\n");
    }

    @Test
    public void shouldGetDeserializeCodeBlockWhenNotNestedUnderAttribute() {
        RepresentsSubClassesAnnotation representsSubClassesAnnotation = new RepresentsSubClassesAnnotation("type",
                "", Arrays.asList(guestSubClassInfo, adminSubClassInfo));

        CodeBlock deserializeCodeBlock = representsSubClassesAnnotation.getDeserializeCodeBlock(context, userRepresenterAnnotation);

        assertThat(deserializeCodeBlock.toString()).isEqualToNormalizingNewlines("" +
                "com.tw.User model = null;\n" +
                "java.lang.String type = (java.lang.String) json.get(\"type\");\n" +
                "if (type.equals(\"guest\")) {\n" +
                "  model = com.tw.representers.gen.GuestUserMapper.fromJSON(json);\n" +
                "}\n" +
                "else if (type.equals(\"admin\")) {\n" +
                "  model = com.tw.representers.gen.AdminUserMapper.fromJSON(json);\n" +
                "}\n" +
                "else {\n" +
                "  throw new java.lang.RuntimeException(\"Could not find any subclass for specified type. Possible values are: guest,admin\");\n" +
                "}\n");
    }

}
