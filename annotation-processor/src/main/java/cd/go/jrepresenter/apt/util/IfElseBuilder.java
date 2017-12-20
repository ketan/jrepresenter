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

import com.squareup.javapoet.CodeBlock;

public class IfElseBuilder {

    private final CodeBlock.Builder builder;
    private State currentState;

    private enum State {
        IF,
        ELSE_IF,
        ELSE;
    }

    public IfElseBuilder(CodeBlock.Builder builder) {
        this.builder = builder;
        currentState = State.IF;
    }

    public IfElseBuilder addIf(String condition, Object... args) {
        switch (currentState) {
            case IF:
                builder.beginControlFlow("if (" + condition + ")", args);
                currentState = State.ELSE_IF;
                break;
            case ELSE_IF:
                builder.beginControlFlow("else if (" + condition + ")", args);
                break;
            case ELSE:
                throw new RuntimeException("Cannot add more conditions after 'else'");
            default:
                throw new IllegalStateException("Unknown state: " + currentState);
        }
        return this;
    }

    public IfElseBuilder withBody(String statement, Object... args) {
        builder.addStatement(statement, args)
                .endControlFlow();
        return this;
    }

    public IfElseBuilder withBody(CodeBlock codeBlock) {
        builder.add(codeBlock)
                .endControlFlow();
        return this;
    }

    public void addElse(String statement, Object... args) {
        builder.beginControlFlow("else")
                .addStatement(statement, args)
                .endControlFlow();
        this.currentState = State.ELSE;
    }

}
