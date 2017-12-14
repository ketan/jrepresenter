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

package cd.go.jrepresenter.examples;

import java.util.List;

public class PipelineGroup {
    private CaseInsensitiveString name;
    private CaseInsensitiveString permissions;
    private List<Pipeline> pipelines;

    public PipelineGroup(String name, String permissions, List<Pipeline> pipelines) {
        this.name = new CaseInsensitiveString(name);
        this.permissions = new CaseInsensitiveString(permissions);
        this.pipelines = pipelines;
    }

    public PipelineGroup() {
    }

    public CaseInsensitiveString getName() {
        return name;
    }

    public void setName(CaseInsensitiveString name) {
        this.name = name;
    }

    public List<Pipeline> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }

    public CaseInsensitiveString getPermissions() {
        return permissions;
    }

    public void setPermissions(CaseInsensitiveString permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "PipelineGroup{" +
                "name=" + name +
                ", permissions=" + permissions +
                ", pipelines=" + pipelines +
                '}';
    }
}
