/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.matcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.regex.Matcher;

import static com.samebug.clients.matcher.LineType.*;

abstract class Line {
    @Nonnull
    public LineType getType() {
        return type;
    }

    @Nonnull
    public String getRaw() {
        return raw;
    }

    @Nonnull
    private final String raw;

    @Nonnull
    private final LineType type;

    Line(@Nonnull LineType type, @Nonnull String raw) {
        this.raw = raw;
        this.type = type;
    }
}


class StackFrame extends Line {
    public StackFrame(LineType type, Matcher matcher) {
        super(StackFrameType,  matcher.group(0));
        this.packageName =  matcher.group(1);
        this.className =  matcher.group(2);
        this.methodName =  matcher.group(3);
        this.location =  matcher.group(4);
    }

    @Nullable
    private final String packageName;

    @Nonnull
    private final String className;

    @Nonnull
    private final String methodName;

    @Nonnull
    private final String location;
}

class ExceptionStart extends Line {
    public ExceptionStart(LineType type, Matcher matcher) {
        super(type, matcher.group(0));

        this.packageName = matcher.group(1);
        this.className = matcher.group(2);
        this.message = type == ExceptionStartTypeWithMessage ? matcher.group(3) : null;
    }

    @Nonnull
    private final String packageName;

    @Nonnull
    private final String className;

    @Nullable
    private final String message;

}

class CausedBy extends Line {
    public CausedBy(LineType type, Matcher matcher) {
        super(type, matcher.group(0));
        this.packageName = matcher.group(1);
        this.className = matcher.group(2);
        this.message = type == CausedByTypeWithMessage ? matcher.group(3) : null;
    }

    @Nonnull
    private final String packageName;

    @Nonnull
    private final String className;

    @Nullable
    private final String message;
}


class More extends Line {
    private final int commonFrames;

    public More(LineType type, Matcher matcher) {
        super(MoreType, matcher.group(0));
        this.commonFrames = Integer.parseInt(matcher.group(1));
    }
}

class Message extends Line {
    public Message(LineType type, Matcher matcher) {
        super(type, matcher.group(0));
    }
}