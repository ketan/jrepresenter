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

package cd.go.jrepresenter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.function.Function;

@Target(ElementType.METHOD)
public @interface Property {
    String modelAttributeName() default "";

    Class<?> modelAttributeType();

    Class<? extends Function> serializer() default NullFunction.class;

    Class<? extends Function> deserializer() default NullFunction.class;

    class NullFunction implements Function<Object, Object> {

        @Override
        public Object apply(Object o) {
            return null;
        }
    }
}
