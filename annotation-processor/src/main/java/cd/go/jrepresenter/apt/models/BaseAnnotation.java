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

import com.squareup.javapoet.CodeBlock;

public abstract class BaseAnnotation {
    protected final Attribute modelAttribute;
    protected final Attribute jsonAttribute;

    protected RepresenterAnnotation parent;
    protected boolean embedded;

    BaseAnnotation(Attribute modelAttribute, Attribute jsonAttribute) {
        this.modelAttribute = modelAttribute;
        this.jsonAttribute = jsonAttribute;
    }

    public abstract CodeBlock getSerializeCodeBlock(ClassToAnnotationMap classToAnnotationMap, String jsonVariableName);

    public abstract CodeBlock getDeserializeCodeBlock(ClassToAnnotationMap classToAnnotationMap);


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

}
