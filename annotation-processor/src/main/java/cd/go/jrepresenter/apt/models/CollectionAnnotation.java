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

import java.util.List;

public class CollectionAnnotation extends BaseAnnotation {

    private final ClassName representerClassName;

    public CollectionAnnotation(String representerClassName, Attribute modelAttribute, Attribute jsonAttribute) {
        super(modelAttribute, jsonAttribute);
        this.representerClassName = ClassName.bestGuess(representerClassName);
    }

    @Override
    public CodeBlock getSerializeCodeBlock(ClassToAnnotationMap context, String jsonVariableName) {
        return CodeBlock.builder()
                .addStatement("$N.put($S, $T.toJSON(($T) $N.$N(), requestContext))", jsonVariableName, jsonAttribute.name, context.representerForClass(this.representerClassName).mapperClassImplRelocated(), List.class, "value", modelAttributeGetter())
                .build();
    }

    @Override
    public CodeBlock getDeserializeCodeBlock(ClassToAnnotationMap classToAnnotationMap) {
        ClassName mapperClass = classToAnnotationMap.representerForClass(representerClassName).mapperClassImplRelocated();
        return CodeBlock.builder()
                .beginControlFlow("if (json.containsKey($S))", jsonAttribute.name)
                .addStatement("model.$N($T.fromJson((List) json.get($S)))", modelAttributeSetter(), mapperClass, jsonAttribute.name)
                .endControlFlow()
                .build();
    }

}
