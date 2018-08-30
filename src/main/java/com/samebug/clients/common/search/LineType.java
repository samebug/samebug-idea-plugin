/*
 * Copyright 2018 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum LineType {
    StackFrameType(Regexes.PossiblyFrameRegex),
    CausedByType(Regexes.CausedByRegex),
    MoreType(Regexes.CommonFramesRegex),
    MessageType(Pattern.compile(".*"));

    private final Pattern pattern;


    LineType(Pattern regex) {
        this.pattern = regex;
    }

    public static LineType match(String line) {
        for (LineType lineType : values()) {
            Matcher matcher = lineType.pattern.matcher(line);
            if (matcher.find()) return lineType;
        }
        throw new IllegalStateException("Message line type should match any input!");
    }
}

final class Regexes {
    static final Pattern SpaceRegex = Pattern.compile("[ \\t\\x0B\\xA0]");
    static final Pattern IdentifierRegex = Pattern.compile("(?:\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)");
    static final Pattern ExceptionClassNameRegex = Pattern.compile("(?:[A-Z]\\p{javaJavaIdentifierPart}*)");
    static final Pattern ExceptionTypeRegex = Pattern.compile(String.format("((?:%s\\.)+%s)",
            IdentifierRegex, ExceptionClassNameRegex));
    static final Pattern CausedByRegex = Pattern.compile(String.format("(Caused [bB]y:)\\s+%s",
            ExceptionTypeRegex));
    static final Pattern CommonFramesRegex = Pattern.compile("\\.\\.\\.\\s+(\\d+)\\s+(?:more|common frames omitted)");
    static final Pattern PossiblyCallRegex = Pattern.compile("(?:[\\p{javaJavaIdentifierStart}<][\\p{javaJavaIdentifierPart}>]*)");
    static final Pattern PossiblyLocationRegex = Pattern.compile("\\(([^\\)]*)\\)");
    static final Pattern PossiblyJarRegex = Pattern.compile(String.format("(?:%s~|%s|~|)\\[([^\\]]*)\\]",
            SpaceRegex, SpaceRegex));
    static final Pattern PossiblyFrameRegex = Pattern.compile(String.format("at%s+((?:%s\\.)+(?:%s)?)%s(?:%s)?",
            SpaceRegex, IdentifierRegex, PossiblyCallRegex, PossiblyLocationRegex, PossiblyJarRegex));

    private Regexes() {}
}
