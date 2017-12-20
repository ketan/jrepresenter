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
import cd.go.jrepresenter.LinksMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

public class SubClassInfoAnnotation {
    private final TypeName representerClass;
    private final String value;
    private final TypeName linksProvider;

    public SubClassInfoAnnotation(TypeName representerClass, String value, TypeName linksProvider) {
        this.representerClass = representerClass;
        this.value = value;
        this.linksProvider = linksProvider == null ? ClassName.get(EmptyLinksProvider.class) : linksProvider;
    }

    public TypeName getRepresenterClass() {
        return representerClass;
    }

    public String getValue() {
        return value;
    }

    public CodeBlock getSerializeCodeBlock(RepresenterAnnotation subClassRepresenterAnnotation) {
        CodeBlock.Builder builder = CodeBlock.builder();
        ClassName subClassModel = subClassRepresenterAnnotation.getModelClass();
        if (this.hasLinksProvider()) {
            builder.addStatement("json.putAll($T.toJSON(new $T(), ($T) value, requestContext))", LinksMapper.class, linksProvider, subClassModel);
        }
        return builder
                .addStatement("subClassProperties = $T.toJSON(($T) value, requestContext)", subClassRepresenterAnnotation.mapperClassImplRelocated(), subClassModel)
                .build();
    }

    private boolean hasLinksProvider() {
        return !linksProvider.equals(ClassName.get(EmptyLinksProvider.class));
    }
}
