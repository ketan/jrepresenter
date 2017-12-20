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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClassToAnnotationMap {
    private Map<RepresenterAnnotation, List<BaseAnnotation>> classToAnnotationMap = new LinkedHashMap<>();

    public void add(RepresenterAnnotation representerAnnotation) {
        if (!classToAnnotationMap.containsKey(representerAnnotation)) {
            classToAnnotationMap.put(representerAnnotation, new ArrayList<>());
        }
    }


    public RepresenterAnnotation findRepresenterAnnotation(TypeName representerClass) {
        return classToAnnotationMap.keySet().stream()
                .filter(representerAnnotation -> representerAnnotation.getRepresenterClass().equals(representerClass))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find representer for class " + representerClass));
    }

    public void addAnnotatedMethod(String representerClass, BaseAnnotation propertyAnnotation) {
        addAnnotatedMethod(ClassName.bestGuess(representerClass), propertyAnnotation);
    }

    public void addAnnotatedMethod(TypeName representerClass, BaseAnnotation propertyAnnotation) {
        classToAnnotationMap.forEach((representerAnnotation, baseAnnotations) -> {
            if (representerAnnotation.getRepresenterClass().equals(representerClass)) {
                propertyAnnotation.setParent(representerAnnotation);
                baseAnnotations.add(propertyAnnotation);
            }
        });
    }

    public void forEach(BiConsumer<? super RepresenterAnnotation, ? super List<BaseAnnotation>> action) {
        classToAnnotationMap.forEach(action);
    }

    public void forEach(Consumer<? super RepresenterAnnotation> action) {
        classToAnnotationMap.keySet().forEach(action);
    }

    public List<BaseAnnotation> getAnnotationsOn(RepresenterAnnotation representerAnnotation) {
        return classToAnnotationMap.get(representerAnnotation);
    }
}
