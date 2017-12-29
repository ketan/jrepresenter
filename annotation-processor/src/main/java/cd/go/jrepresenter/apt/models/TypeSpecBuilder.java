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

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.Set;

import static cd.go.jrepresenter.apt.models.MapperJavaConstantsFile.CONSTANTS_CLASS_NAME;

public class TypeSpecBuilder {
    final String constName;
    private final String replacePrefix;

    public TypeSpecBuilder(String constName, String replacePrefix) {
        this.constName = constName;
        this.replacePrefix = replacePrefix;
    }

    public TypeSpec build(Set<TypeName> types) {
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(constName).addModifiers(Modifier.STATIC, Modifier.PUBLIC);
        types.forEach(typeName -> {
            FieldSpec.Builder fieldSpec = FieldSpec.builder(typeName, internalFieldName(typeName))
                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                    .initializer(CodeBlock.builder().add("new $T()", typeName).build());
            builder.addField(fieldSpec.build());
        });
        return builder.build();
    }


    public TypeName fieldName(TypeName typeName) {
        return CONSTANTS_CLASS_NAME.nestedClass(constName).nestedClass(internalFieldName(typeName));
    }

    private String internalFieldName(TypeName typeName) {
        String fieldName;

        if (typeName instanceof ClassName) {
            fieldName = ((ClassName) typeName).simpleName();
        } else {
            fieldName = typeName.toString().replace(".", "_");
        }

        fieldName = fieldName.replaceAll(replacePrefix, "");

        fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldName);
        return fieldName;
    }
}
