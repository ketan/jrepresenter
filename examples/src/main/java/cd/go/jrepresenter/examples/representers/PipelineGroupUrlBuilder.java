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

import cd.go.jrepresenter.RequestContext;
import cd.go.jrepresenter.examples.PipelineGroup;
import cd.go.jrepresenter.Link;
import cd.go.jrepresenter.LinksProvider;

import java.util.Collections;
import java.util.List;

public class PipelineGroupUrlBuilder implements LinksProvider<PipelineGroup> {

    @Override
    public List<Link> getLinks(PipelineGroup model, RequestContext requestContext) {
        Link self = new Link("self", "http://example.com/go/pipeline-groups");
        return Collections.singletonList(self);
    }
}
