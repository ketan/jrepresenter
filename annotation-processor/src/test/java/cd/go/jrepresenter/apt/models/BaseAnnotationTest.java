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
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class BaseAnnotationTest {

    @Test
    public void shouldRenderCodeBlockAsIsIfSkipParseAndRenderAreFalse() {
        BaseAnnotation baseAnnotation = BaseAnnotationBuilder.aBaseAnnotation()
                .withSkipParse(BaseAnnotation.FALSE_FUNCTION)
                .withSkipRender(BaseAnnotation.FALSE_FUNCTION)
                .build();
        Assertions.assertThat(baseAnnotation.getSerializeCodeBlock(null, null).toString()).isEqualToNormalizingNewlines("//todo serialize;\n");
        Assertions.assertThat(baseAnnotation.getDeserializeCodeBlock(null).toString()).isEqualToNormalizingNewlines("//todo deserialize;\n");
    }

    @Test
    public void shouldRenderNothingIfSkipParseAndRenderAreTrue() {
        BaseAnnotation baseAnnotation = BaseAnnotationBuilder.aBaseAnnotation()
                .withSkipParse(BaseAnnotation.TRUE_FUNCTION)
                .withSkipRender(BaseAnnotation.TRUE_FUNCTION)
                .build();

        Assertions.assertThat(baseAnnotation.getSerializeCodeBlock(null, null).toString()).isEqualTo("");
        Assertions.assertThat(baseAnnotation.getDeserializeCodeBlock(null).toString()).isEqualTo("");
    }

    @Test
    public void shouldRenderConditionalIfSkipParseAndRenderAreSetToAnyOtherFunction() {
        BaseAnnotation baseAnnotation = BaseAnnotationBuilder.aBaseAnnotation()
                .withSkipParse(ClassName.bestGuess("com.tw.SkipParse"))
                .withSkipRender(ClassName.bestGuess("com.tw.SkipRender"))
                .build();

        Assertions.assertThat(baseAnnotation.getSerializeCodeBlock(null, null).toString()).isEqualToNormalizingNewlines("" +
                "if (new com.tw.SkipRender().apply(value)) {\n" +
                "  //todo serialize;\n" +
                "}\n");

        Assertions.assertThat(baseAnnotation.getDeserializeCodeBlock(null).toString()).isEqualToNormalizingNewlines("" +
                "if (new com.tw.SkipParse().apply(value)) {\n" +
                "  //todo deserialize;\n" +
                "}\n");
    }
}
