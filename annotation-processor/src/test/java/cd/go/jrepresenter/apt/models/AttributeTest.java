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

import com.squareup.javapoet.TypeName;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class AttributeTest {

    @Test
    public void shouldGetNameAsSnakeCase() {
        Attribute attribute = new Attribute("pipelineId", TypeName.INT);
        assertThat(attribute.nameAsSnakeCase()).isEqualTo("pipeline_id");

        //acronyms aren't supported by guava
        attribute = new Attribute("foobarDAO", TypeName.INT);
        assertThat(attribute.nameAsSnakeCase()).isEqualTo("foobar_d_a_o");
    }
}
