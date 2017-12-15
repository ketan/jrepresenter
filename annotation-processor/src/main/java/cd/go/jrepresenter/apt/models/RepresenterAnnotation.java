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
import cd.go.jrepresenter.EmptyLinksProvider;

public class RepresenterAnnotation {
    private static final String MAPPER_CLASS_SUFFIX = "Mapper";
    private static final String PACKAGE_NAME_SUFFIX = ".gen";

    private final ClassName representerClass;
    private final ClassName modelClass;
    private final ClassName linksBuilderClass;
    private final boolean skipSerialize;
    private final boolean skipDeserialize;

    public RepresenterAnnotation(String representerClass, String modelClass, String linksBuilderClass, boolean skipSerialize, boolean skipDeserialize) {
        this.representerClass = ClassName.bestGuess(representerClass);
        this.modelClass = ClassName.bestGuess(modelClass);
        this.linksBuilderClass = ClassName.bestGuess(linksBuilderClass);
        this.skipSerialize = skipSerialize;
        this.skipDeserialize = skipDeserialize;
    }

    public ClassName getRepresenterClass() {
        return representerClass;
    }

    public ClassName getModelClass() {
        return modelClass;
    }

    public ClassName getLinksBuilderClass() {
        return linksBuilderClass;
    }

    public boolean shouldSkipSerialize() {
        return skipSerialize;
    }

    public boolean shouldSkipDeserialize() {
        return skipDeserialize;
    }

    protected String packageNameRelocated() {
        return getRepresenterClass().packageName() + PACKAGE_NAME_SUFFIX;
    }

    String mapperClassImplSimpleName() {
        return getModelClass().simpleName() + MAPPER_CLASS_SUFFIX;
    }

    public ClassName mapperClassImplRelocated() {
        return ClassName.bestGuess(packageNameRelocated() + "." + getModelClass().simpleName() + MAPPER_CLASS_SUFFIX);
    }

    public boolean hasLinksProvider() {
        return !getLinksBuilderClass().equals(ClassName.get(EmptyLinksProvider.class));
    }
}
