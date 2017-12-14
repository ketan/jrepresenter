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

import java.sql.Timestamp;

public class Stage {

    private CaseInsensitiveString name;
    private Timestamp createdTime;
    private StageState state;

    public Stage(String name, Timestamp createdTime, StageState state) {
        this.name = new CaseInsensitiveString(name);
        this.createdTime = createdTime;
        this.state = state;
    }

    public Stage() {


    }

    public CaseInsensitiveString getName() {
        return name;
    }

    public void setName(CaseInsensitiveString name) {
        this.name = name;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public StageState getState() {
        return state;
    }

    public void setState(StageState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Stage{" +
                "name=" + name +
                ", createdTime=" + createdTime +
                ", state=" + state +
                '}';
    }
}
