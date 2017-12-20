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

import cd.go.jrepresenter.util.FalseFunction;
import cd.go.jrepresenter.util.NullBiConsumer;
import cd.go.jrepresenter.util.NullFunction;
import cd.go.jrepresenter.util.TrueFunction;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

public abstract class BaseAnnotation {
    protected static final ClassName NULL_FUNCTION = ClassName.get(NullFunction.class);
    protected static final ClassName NULL_BICONSUMER = ClassName.get(NullBiConsumer.class);
    protected static final ClassName TRUE_FUNCTION = ClassName.get(TrueFunction.class);
    protected static final ClassName FALSE_FUNCTION = ClassName.get(FalseFunction.class);
    public static final ClassName VOID_CLASS = ClassName.get(Void.class);

    protected final Attribute modelAttribute;
    protected final Attribute jsonAttribute;
    protected final TypeName representerClassName;
    protected final TypeName serializerClassName;
    protected final TypeName deserializerClassName;
    protected final TypeName getterClassName;
    protected final TypeName setterClassName;

    protected final TypeName skipParse;
    protected final TypeName skipRender;

    protected RepresenterAnnotation parent;
    protected boolean embedded;

    BaseAnnotation(Attribute modelAttribute, Attribute jsonAttribute, TypeName representerClassName, TypeName serializerClassName, TypeName deserializerClassName, TypeName getterClassName, TypeName setterClassName, TypeName skipParse, TypeName skipRender) {
        this.modelAttribute = modelAttribute;
        this.jsonAttribute = jsonAttribute;
        this.representerClassName = representerClassName == null ? VOID_CLASS : representerClassName;
        this.serializerClassName = serializerClassName == null ? NULL_FUNCTION : serializerClassName;
        this.deserializerClassName = deserializerClassName == null ? NULL_FUNCTION : deserializerClassName;
        this.getterClassName = getterClassName == null ? NULL_FUNCTION : getterClassName;
        this.setterClassName = setterClassName == null ? NULL_BICONSUMER : setterClassName;
        this.skipParse = skipParse == null ? FALSE_FUNCTION : skipParse;
        this.skipRender = skipRender == null ? FALSE_FUNCTION : skipRender;
    }

    public final CodeBlock getSerializeCodeBlock(ClassToAnnotationMap classToAnnotationMap, String jsonVariableName) {
        if (skipRender.equals(FALSE_FUNCTION)) {
            return doSetSerializeCodeBlock(classToAnnotationMap, jsonVariableName);
        } else if (skipRender.equals(TRUE_FUNCTION)) {
            return CodeBlock.builder().build();
        } else {
            return CodeBlock.builder()
                    .beginControlFlow("if (new $T().apply(value))", skipRender)
                    .add(doSetSerializeCodeBlock(classToAnnotationMap, jsonVariableName))
                    .endControlFlow()
                    .build();

        }
    }

    public final CodeBlock getDeserializeCodeBlock(ClassToAnnotationMap classToAnnotationMap) {
        if (skipParse.equals(FALSE_FUNCTION)) {
            return doGetDeserializeCodeBlock(classToAnnotationMap);
        }
        if (skipParse.equals(TRUE_FUNCTION)) {
            return CodeBlock.builder().build();
        } else {
            return CodeBlock.builder()
                    .beginControlFlow("if (new $T().apply(value))", skipParse)
                    .add(doGetDeserializeCodeBlock(classToAnnotationMap))
                    .endControlFlow()
                    .build();

        }
    }

    protected abstract CodeBlock doSetSerializeCodeBlock(ClassToAnnotationMap classToAnnotationMap, String jsonVariableName);

    public boolean hasRepresenter() {
        return !representerClassName.equals(VOID_CLASS);
    }

    protected boolean hasGetterClass() {
        return !getterClassName.equals(NULL_FUNCTION);
    }

    protected boolean hasSetterClass() {
        return !setterClassName.equals(NULL_BICONSUMER);
    }

    protected CodeBlock applyGetter() {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (hasGetterClass()) {
            return builder.add("new $T().apply(value)", getterClassName).build();
        } else {
            return builder.add("value.$N()", modelAttributeGetter()).build();
        }
    }

    protected CodeBlock applyRenderRepresenter(ClassToAnnotationMap context, CodeBlock getterWithSerializer) {
        if (hasRepresenter()) {
            CodeBlock.Builder builder = CodeBlock.builder();
            ClassName mapperClass = context.findRepresenterAnnotation(representerClassName).mapperClassImplRelocated();
            builder.add("$T.toJSON(", mapperClass)
                    .add(getterWithSerializer)
                    .add(", requestContext)");
            return builder.build();
        } else {
            return getterWithSerializer;
        }
    }

    protected CodeBlock putInJson(String jsonVariableName, CodeBlock whatToPut) {
        return CodeBlock.builder()
                .add("$[")
                .add("$N.put($S, ", jsonVariableName, jsonAttribute.nameAsSnakeCase())
                .add(whatToPut)
                .add(");\n$]")
                .build();
    }

    protected abstract CodeBlock doGetDeserializeCodeBlock(ClassToAnnotationMap classToAnnotationMap);

    protected String modelAttributeGetter() {
        return "get" + modelAttribute.name.substring(0, 1).toUpperCase() + modelAttribute.name.substring(1);
    }

    protected String modelAttributeSetter() {
        return "set" + modelAttribute.name.substring(0, 1).toUpperCase() + modelAttribute.name.substring(1);
    }

    public void setParent(RepresenterAnnotation parent) {
        this.parent = parent;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    protected boolean hasSerializer() {
        return !serializerClassName.equals(NULL_FUNCTION);
    }

    protected boolean hasDeserializer() {
        return !deserializerClassName.equals(NULL_FUNCTION);
    }


    protected CodeBlock applySetter(CodeBlock codeToSet) {
        if (hasSetterClass()) {
            return CodeBlock.builder()
                    .add("new $T().accept(model, ", setterClassName)
                    .add(codeToSet)
                    .add(")")
                    .build();
        } else {
            return CodeBlock.builder()
                    .add("model.$N(", modelAttributeSetter())
                    .add(codeToSet)
                    .add(")")
                    .build();
        }
    }

    protected CodeBlock applyParseRepresenter(ClassToAnnotationMap context, CodeBlock deserializedCodeBlock) {
        if (hasRepresenter()) {
            ClassName mapperClass = context.findRepresenterAnnotation(representerClassName).mapperClassImplRelocated();
            return CodeBlock.builder()
                    .add("$T.fromJSON(", mapperClass)
                    .add(deserializedCodeBlock)
                    .add(")")
                    .build();
        } else {
            return deserializedCodeBlock;
        }
    }
}
