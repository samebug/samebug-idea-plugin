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
package com.samebug.clients.search.matcher;

import com.samebug.clients.search.api.LogScanner;
import com.samebug.clients.search.api.StackTraceListener;
import com.samebug.clients.search.api.entities.tracking.DebugSessionInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Scans the log for stacktrace.
 * <p/>
 * When a stacktrace is found, notifies the StackTraceListener.
 */
final public class StackTraceMatcher extends MatcherStateMachine implements LogScanner {
    private final DebugSessionInfo sessionInfo;
    private final StackTraceListener listener;

    public StackTraceMatcher(StackTraceListener listener, @Nullable DebugSessionInfo sessionInfo) {
        super();
        this.listener = listener;
        this.sessionInfo = sessionInfo;
    }

    @Override
    public void line(String line) {
        step(line);
    }

    @Override
    public void end() {
        stop();
    }

    @Override
    protected void stackTraceFound(final List<String> stackTraceLines) {
        StringBuilder b = new StringBuilder();
        boolean first = true;
        for (String line : stackTraceLines) {
            if (!first) b.append("\n");
            else first = false;
            b.append(line);
        }
        listener.stacktraceFound(sessionInfo, b.toString());
    }

    private final static Pattern SpaceRegex = Pattern.compile("[ \\t\\x0B\\xA0]");
    private final static Pattern IdentifierRegex = Pattern.compile("(?:\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)");
    private final static Pattern ExceptionClassNameRegex = Pattern.compile("(?:[A-Z]\\p{javaJavaIdentifierPart}*)");
    private final static Pattern ExceptionTypeRegex = Pattern.compile(String.format("((?:%s\\.)+%s)",
            IdentifierRegex, ExceptionClassNameRegex));
    private final static Pattern CausedByRegex = Pattern.compile(String.format("(Caused [bB]y:)\\s+%s",
            ExceptionTypeRegex));
    private final static Pattern CommonFramesRegex = Pattern.compile("\\.\\.\\.\\s+(\\d+)\\s+(?:more|common frames omitted)");
    private final static Pattern PossiblyCallRegex = Pattern.compile("(?:[\\p{javaJavaIdentifierStart}<][\\p{javaJavaIdentifierPart}>]*)");
    private final static Pattern PossiblyLocationRegex = Pattern.compile("\\(([^\\)]*)\\)");
    private final static Pattern PossiblyJarRegex = Pattern.compile(String.format("(?:%s~|%s|~|)\\[([^\\]]*)\\]",
            SpaceRegex, SpaceRegex));
    private final static Pattern PossiblyFrameRegex = Pattern.compile(String.format("at%s+((?:%s\\.)+(?:%s)?)%s(?:%s)?",
            SpaceRegex, IdentifierRegex, PossiblyCallRegex, PossiblyLocationRegex, PossiblyJarRegex));

}

