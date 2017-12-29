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

package cd.go.jrepresenter.apt.util;

import com.google.common.base.Strings;
import com.squareup.javapoet.CodeBlock;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Arrays;

public class DebugStatement {
    private static boolean DEBUG = false;
    private static ProcessingEnvironment processingEnv;

    public static CodeBlock printDebug(Object... messages) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            String fileName = stackTraceElement.getFileName();
            int lineNumber = stackTraceElement.getLineNumber();

            String callerLocation;
            if (fileName != null && lineNumber >= 0) {
                callerLocation = "(" + fileName + ":" + lineNumber + ")";
            } else if (fileName != null) {
                callerLocation = "(" + fileName + ")";
            } else {
                callerLocation = "(Unknown Source)";
            }

            builder.add("/* ");
            builder.add("DEBUG: $N: ", callerLocation);

            if (messages != null && messages.length > 0) {
                builder.add(Strings.repeat("$N ", messages.length), Arrays.stream(messages).map(Object::toString).toArray(Object[]::new));
            }
            builder.add(" */\n");
        }
        return builder.build();
    }

    public static void enable() {
        DEBUG = true;
    }
}
