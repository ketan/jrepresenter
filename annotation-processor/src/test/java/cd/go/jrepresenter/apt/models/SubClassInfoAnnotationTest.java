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
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SubClassInfoAnnotationTest {

    private ClassName representerClass;
    private ClassName linksProvider;
    private ClassName modelClass;
    private RepresenterAnnotation subClassRepresenterAnnotation;

    @Before
    public void setUp() throws Exception {
        representerClass = ClassName.bestGuess("com.tw.GuestUserRepresenter");
        linksProvider = ClassName.bestGuess("com.tw.GuestUserLinksProvider");
        modelClass = ClassName.bestGuess("com.tw.GuestUser");
        subClassRepresenterAnnotation = new RepresenterAnnotation(representerClass, modelClass, null, false, false);
    }

    @Test
    public void shouldGetSerializeCodeBlockWithLinks() {
        SubClassInfoAnnotation subClassInfoAnnotation = new SubClassInfoAnnotation(representerClass, "guest", linksProvider);

        CodeBlock serializeCodeBlock = subClassInfoAnnotation.getSerializeCodeBlock(subClassRepresenterAnnotation);

        assertThat(serializeCodeBlock.toString()).isEqualToNormalizingNewlines("" +
                "json.putAll(cd.go.jrepresenter.LinksMapper.toJSON(new com.tw.GuestUserLinksProvider(), (com.tw.GuestUser) value, requestContext));\n" +
                "subClassProperties = com.tw.gen.GuestUserMapper.toJSON((com.tw.GuestUser) value, requestContext);" +
                "\n");
    }

    @Test
    public void shouldGetSerializeCodeBlockWithoutLinks() {
        SubClassInfoAnnotation subClassInfoAnnotation = new SubClassInfoAnnotation(representerClass, "guest", null);

        CodeBlock serializeCodeBlock = subClassInfoAnnotation.getSerializeCodeBlock(subClassRepresenterAnnotation);

        assertThat(serializeCodeBlock.toString()).isEqualToNormalizingNewlines("" +
                "subClassProperties = com.tw.gen.GuestUserMapper.toJSON((com.tw.GuestUser) value, requestContext);" +
                "\n");
    }

}
