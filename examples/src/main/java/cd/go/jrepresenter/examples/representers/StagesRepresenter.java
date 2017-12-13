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

package cd.go.jrepresenter.examples.representers;

import cd.go.jrepresenter.examples.CaseInsensitiveString;
import cd.go.jrepresenter.examples.Stage;
import cd.go.jrepresenter.examples.StageState;
import cd.go.jrepresenter.annotations.Property;
import cd.go.jrepresenter.annotations.Represents;
import cd.go.jrepresenter.examples.serializers.CaseInsensitiveStringDeserializer;
import cd.go.jrepresenter.examples.serializers.CaseInsensitiveStringSerializer;

import java.sql.Timestamp;

@Represents(Stage.class)
public interface StagesRepresenter {
    @Property(serializer = CaseInsensitiveStringSerializer.class, deserializer = CaseInsensitiveStringDeserializer.class, modelAttributeType = CaseInsensitiveString.class)
    public String name();

    @Property(modelAttributeName = "createdTime", modelAttributeType = Timestamp.class)
    public String createdAt();

    @Property(modelAttributeType = StageState.class)
    public String state();

}
