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
import com.squareup.javapoet.TypeName;
import cd.go.jrepresenter.EmptyLinksProvider;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapperJavaSourceFileTest {

    @Test
    public void shouldSerializeSimpleObjectProperties() throws Exception {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation("com.foo.UserRepresenter", "com.foo.User", EmptyLinksProvider.class.getName());
        Attribute modelAttribute = new Attribute("fname", TypeName.get(String.class));
        Attribute jsonAttribute = new Attribute("firstName", TypeName.get(String.class));
        PropertyAnnotation propertyAnnotation = new PropertyAnnotation(modelAttribute, jsonAttribute, null, null);
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.UserRepresenter", propertyAnnotation);
        MapperJavaSourceFile mapperJavaSourceFile = new MapperJavaSourceFile(representerAnnotation, context);

        assertThat(mapperJavaSourceFile.toSource()).isEqualToNormalizingNewlines("" +
                "package com.foo.gen;\n" +
                "\n" +
                "import cd.go.jrepresenter.RequestContext;\n" +
                "import com.foo.User;\n" +
                "import java.lang.String;\n" +
                "import java.util.LinkedHashMap;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "public class UserMapper {\n" +
                "  public static Map toJSON(User value, RequestContext requestContext) {\n" +
                "    Map json = new LinkedHashMap();\n" +
                "    json.put(\"firstName\", value.getFname());\n" +
                "    return json;\n" +
                "  }\n" +
                "\n" +
                "  public static List toJSON(List<User> values, RequestContext requestContext) {\n" +
                "    return values.stream().map(eachItem -> UserMapper.toJSON(eachItem, requestContext)).collect(Collectors.toList());\n" +
                "  }\n" +
                "\n" +
                "  public static User fromJSON(Map json) {\n" +
                "    User model = new User();\n" +
                "    if (json.containsKey(\"firstName\")) {\n" +
                "      model.setFname((String) json.get(\"firstName\"));\n" +
                "    }\n" +
                "    return model;\n" +
                "  }\n" +
                "\n" +
                "  public static List<User> fromJSON(List<Map> jsonArray) {\n" +
                "    return jsonArray.stream().map(eachItem -> UserMapper.fromJSON(eachItem)).collect(Collectors.toList());\n" +
                "  }" +
                "\n" +
                "}\n");
    }

    @Test
    public void shouldSerializeSimpleObjectPropertiesAsEmbedded() throws Exception {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation("com.foo.UserRepresenter", "com.foo.User", EmptyLinksProvider.class.getName());
        Attribute modelAttribute = new Attribute("fname", TypeName.get(String.class));
        Attribute jsonAttribute = new Attribute("firstName", TypeName.get(String.class));
        PropertyAnnotation propertyAnnotation = new PropertyAnnotation(modelAttribute, jsonAttribute, null, null);
        propertyAnnotation.setEmbedded(true);
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.UserRepresenter", propertyAnnotation);
        MapperJavaSourceFile mapperJavaSourceFile = new MapperJavaSourceFile(representerAnnotation, context);

        assertThat(mapperJavaSourceFile.toSource()).isEqualToNormalizingNewlines("" +
                "package com.foo.gen;\n" +
                "\n" +
                "import cd.go.jrepresenter.RequestContext;\n" +
                "import com.foo.User;\n" +
                "import java.lang.String;\n" +
                "import java.util.LinkedHashMap;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "public class UserMapper {\n" +
                "  public static Map toJSON(User value, RequestContext requestContext) {\n" +
                "    Map json = new LinkedHashMap();\n" +
                "    Map embeddedMap = new LinkedHashMap();\n" +
                "    embeddedMap.put(\"firstName\", value.getFname());\n" +
                "    json.put(\"_embedded\", embeddedMap);\n" +
                "    return json;\n" +
                "  }\n" +
                "\n" +
                "  public static List toJSON(List<User> values, RequestContext requestContext) {\n" +
                "    return values.stream().map(eachItem -> UserMapper.toJSON(eachItem, requestContext)).collect(Collectors.toList());\n" +
                "  }\n" +
                "\n" +
                "  public static User fromJSON(Map json) {\n" +
                "    User model = new User();\n" +
                "    if (json.containsKey(\"firstName\")) {\n" +
                "      model.setFname((String) json.get(\"firstName\"));\n" +
                "    }\n" +
                "    return model;\n" +
                "  }\n" +
                "\n" +
                "  public static List<User> fromJSON(List<Map> jsonArray) {\n" +
                "    return jsonArray.stream().map(eachItem -> UserMapper.fromJSON(eachItem)).collect(Collectors.toList());\n" +
                "  }\n" +
                "}\n");
    }
}
