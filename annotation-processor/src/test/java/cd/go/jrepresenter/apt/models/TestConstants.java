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

import cd.go.jrepresenter.EmptyLinksProvider;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

public class TestConstants {
    static final ClassName USER_REPRESENTER_CLASS = ClassName.bestGuess("com.tw.UserRepresenter");

    static final ClassName USER_MODEL = ClassName.bestGuess("com.tw.User");
    static final ClassName EMPTY_LINKS_PROVIDER = ClassName.bestGuess(EmptyLinksProvider.class.getName());
    static final ClassName CASE_INSENSITIVE_STRING = ClassName.bestGuess("com.tw.CaseInsensitiveString");

    static final ClassName STRING_CLASS = ClassName.get(String.class);
    static final TypeName INT_TYPE = TypeName.get(int.class);
    

    static final ClassName CASE_INSENSITIVE_STRING_SERIALIZER = ClassName.bestGuess("com.tw.CaseInsensitiveStringSerializer");
    static final ClassName CASE_INSENSITIVE_STRING_DESERIALIZER = ClassName.bestGuess("com.tw.CaseInsensitiveStringDeserializer");


    static final ClassName FNAME_GETTER = ClassName.bestGuess("com.tw.FNameGetter");
    static final ClassName TRIGGERED_BY_SETTER = ClassName.bestGuess("com.tw.TriggeredBySetter");
}
