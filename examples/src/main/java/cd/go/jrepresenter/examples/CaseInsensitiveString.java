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

import java.io.Serializable;

public class CaseInsensitiveString implements Comparable<CaseInsensitiveString>, Serializable {

    private final String name;
    private final String lowerCaseName;//used only for comparison

    public CaseInsensitiveString(String name) {
        this.name = name;
        this.lowerCaseName = name == null ? null : name.toLowerCase();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CaseInsensitiveString)) {
            return false;
        }

        CaseInsensitiveString that = (CaseInsensitiveString) o;

        if (name != null ? !toLower().equals(that.toLower()) : that.name != null) {
            return false;
        }

        return true;
    }

    public String toLower() {
        return lowerCaseName;
    }

    @Override
    public int hashCode() {
        return name != null ? toLower().hashCode() : 0;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new CaseInsensitiveString(name);
    }

    public int compareTo(CaseInsensitiveString other) {
        return toLower().compareTo(other.toLower());
    }

}
