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
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RepresenterAnnotationTest {

    @Test
    public void shouldRelocateSourceFiles() throws Exception {
        RepresenterAnnotation representerAnnotation = new RepresenterAnnotation(ClassName.bestGuess("com.foo.representers.UserRepresenter"), ClassName.bestGuess("com.foo.User"), ClassName.bestGuess(EmptyLinksProvider.class.getName()), false, false);


        assertThat(representerAnnotation.getRepresenterClass()).isEqualTo(ClassName.bestGuess("com.foo.representers.UserRepresenter"));
        assertThat(representerAnnotation.getModelClass()).isEqualTo(ClassName.bestGuess("com.foo.User"));

        assertThat(representerAnnotation.mapperClassImplSimpleName()).isEqualTo("UserMapper");
        assertThat(representerAnnotation.mapperClassImplRelocated()).isEqualTo(ClassName.bestGuess("gen.com.foo.representers.UserMapper"));
    }

}
