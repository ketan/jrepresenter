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

import cd.go.jrepresenter.annotations.Collection;
import cd.go.jrepresenter.annotations.Property;
import cd.go.jrepresenter.annotations.Represents;
import cd.go.jrepresenter.apt.models.*;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RepresenterAnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new LinkedHashSet<>(Arrays.asList(Represents.class.getName(), Property.class.getName(), Collection.class.getName()));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ClassToAnnotationMap classToAnnotationMap = new ClassToAnnotationMap();

        roundEnv.getElementsAnnotatedWith(Represents.class).forEach(representerClass -> {
            String modelClassName = representedModel(representerClass);
            String linksBuilderClassName = linksBuilderClass(representerClass);
            classToAnnotationMap.add(new RepresenterAnnotation(representerClass.toString(), modelClassName, linksBuilderClassName));
        });

        roundEnv.getElementsAnnotatedWith(Property.class).forEach(method -> {
            Property annotation = method.getAnnotation(Property.class);

            String jsonAttributeName = getJsonAttributeName(method);
            String modelAttributeName = getModelAttributeName(method, annotation);
            TypeName modelAttributeType = getModelAttributeType(annotation);
            TypeName serializerClassName = getSerializerClassName(annotation);
            TypeName deserializerClassName = getDeserializerClassName(annotation);

            TypeName jsonAttributeType = ClassName.get(((ExecutableType) method.asType()).getReturnType());
            //TODO validate(jsonAttributeName);
            Attribute modelAttribute = new Attribute(modelAttributeName, modelAttributeType);
            Attribute jsonAttribute = new Attribute(jsonAttributeName, jsonAttributeType);
            PropertyAnnotation propertyAnnotation = new PropertyAnnotation(modelAttribute, jsonAttribute, serializerClassName, deserializerClassName);
            classToAnnotationMap.addAnnotatedMethod(ClassName.get(method.getEnclosingElement().asType()), propertyAnnotation);
        });

        roundEnv.getElementsAnnotatedWith(Collection.class).forEach(method -> {
            Collection annotation = method.getAnnotation(Collection.class);

            String jsonAttributeName = getJsonAttributeName(method);
            String modelAttributeName = getModelAttributeName(method, annotation);
            String representerClassName = getRepresenterClassName(annotation);

            TypeName modelAttributeType = ClassName.get(((ExecutableType) method.asType()).getReturnType());
            TypeName jsonAttributeType = ClassName.get(((ExecutableType) method.asType()).getReturnType());

            Attribute modelAttribute = new Attribute(modelAttributeName, modelAttributeType);
            Attribute jsonAttribute = new Attribute(jsonAttributeName, jsonAttributeType);
            CollectionAnnotation propertyAnnotation = new CollectionAnnotation(representerClassName, modelAttribute, jsonAttribute);
            classToAnnotationMap.addAnnotatedMethod(ClassName.bestGuess(method.getEnclosingElement().toString()), propertyAnnotation);
        });

        writeFiles(classToAnnotationMap);

        return true;
    }

    private String getJsonAttributeName(Element method) {
        return method.getSimpleName().toString();
    }

    private String getModelAttributeName(Element method, Property annotation) {
        String modelAttributeName = annotation.modelAttributeName();

        if (modelAttributeName == null || modelAttributeName.trim().equals("")) {
            modelAttributeName = method.getSimpleName().toString();
        }
        return modelAttributeName;
    }

    private String getModelAttributeName(Element method, Collection annotation) {
        String modelAttributeName = annotation.attribute();

        if (modelAttributeName == null || modelAttributeName.trim().equals("")) {
            modelAttributeName = method.getSimpleName().toString();
        }
        return modelAttributeName;
    }

    private TypeName getModelAttributeType(Property annotation) {
        try {
            return TypeName.get(annotation.modelAttributeType());
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            return TypeName.get(classTypeMirror);
        }
    }

    private TypeName getSerializerClassName(Property annotation) {
        try {
            return TypeName.get(annotation.serializer());
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            return TypeName.get(classTypeMirror);
        }
    }

    private TypeName getDeserializerClassName(Property annotation) {
        try {
            return TypeName.get(annotation.deserializer());
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            return TypeName.get(classTypeMirror);
        }
    }

    private String getRepresenterClassName(Collection annotation) {
        String className;
        try {
            className = annotation.representer().getName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            className = classTypeElement.getQualifiedName().toString();
        }
        return className;
    }

    private String representedModel(Element representerClass) {
        Represents representsAnnotation = representerClass.getAnnotation(Represents.class);
        String modelClassName;
        try {
            Class<?> clazz = representsAnnotation.value();
            modelClassName = clazz.getCanonicalName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            modelClassName = classTypeElement.getQualifiedName().toString();
        }
        return modelClassName;
    }

    private String linksBuilderClass(Element representerClass) {
        Represents representsAnnotation = representerClass.getAnnotation(Represents.class);
        String linksBuilderClassName;
        try {
            Class<?> clazz = representsAnnotation.linksBuilder();
            linksBuilderClassName = clazz.getCanonicalName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            linksBuilderClassName = classTypeElement.getQualifiedName().toString();
        }
        return linksBuilderClassName;
    }

    private void writeFiles(ClassToAnnotationMap classToAnnotationMap) {
        classToAnnotationMap.forEach((representerAnnotation) -> {
            try {
                writeMapperFile(classToAnnotationMap, representerAnnotation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void writeMapperFile(ClassToAnnotationMap context, RepresenterAnnotation representerAnnotation) throws IOException {
        MapperJavaSourceFile javaSourceFile = new MapperJavaSourceFile(representerAnnotation, context);
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(javaSourceFile.representerAnnotation.mapperClassImplRelocated().toString());

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.append(javaSourceFile.toSource());
        }
    }

}
