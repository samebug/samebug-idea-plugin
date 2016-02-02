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

import static com.samebug.clients.search.matcher.LineType.*;

enum State {
    WaitingForExceptionStart(ExceptionStartTypeWithMessage, ExceptionStartTypeWithoutMessage) {
        @Override
        protected State nextState(Line matchingLine) {
            switch (matchingLine.getType()) {
                case ExceptionStartTypeWithMessage:
                    return ExceptionStartedWithMessage;
                case ExceptionStartTypeWithoutMessage:
                    return ExceptionStartedWithoutMessage;
                default:
                    throw new IllegalStateException("Unable to parse " + matchingLine.getType().toString() + ": " + matchingLine.getRaw());
            }
        }
    },
    ExceptionStartedWithMessage(StackFrameType, MessageType) {
        @Override
        protected State nextState(Line matchingLine) {
            switch (matchingLine.getType()) {
                case StackFrameType:
                    return StackTraceStarted;
                case MessageType:
                    return ExceptionStartedWithMessage;
                default:
                    throw new IllegalStateException("Unable to parse " + matchingLine.getType().toString() + ": " + matchingLine.getRaw());
            }
        }
    },
    ExceptionStartedWithoutMessage(StackFrameType) {
        @Override
        protected State nextState(Line matchingLine) {
            switch (matchingLine.getType()) {
                case StackFrameType:
                    return StackTraceStarted;
                default:
                    throw new IllegalStateException("Unable to parse " + matchingLine.getType().toString() + ": " + matchingLine.getRaw());
            }
        }
    },
    StackTraceStarted(StackFrameType, MoreType, CausedByTypeWithMessage, CausedByTypeWithoutMessage, MessageType) {
        @Override
        protected State nextState(Line matchingLine) {
            switch (matchingLine.getType()) {
                case StackFrameType:
                    return StackTraceStarted;
                case MoreType:
                    return More;
                case CausedByTypeWithMessage:
                    return ExceptionStartedWithMessage;
                case CausedByTypeWithoutMessage:
                    return ExceptionStartedWithoutMessage;
                case MessageType:
                    return ExceptionFinished;
                default:
                    throw new IllegalStateException("Unable to parse " + matchingLine.getType().toString() + ": " + matchingLine.getRaw());
            }
        }
    },
    More(CausedByTypeWithMessage, CausedByTypeWithoutMessage, MessageType) {
        @Override
        protected State nextState(Line matchingLine) {
            switch (matchingLine.getType()) {
                case CausedByTypeWithMessage:
                    return ExceptionStartedWithMessage;
                case CausedByTypeWithoutMessage:
                    return ExceptionStartedWithoutMessage;
                case MessageType:
                    return ExceptionFinished;
                default:
                    throw new IllegalStateException("Unable to parse " + matchingLine.getType().toString() + ": " + matchingLine.getRaw());
            }
        }
    },
    ExceptionFinished() {
        @Override
        protected State nextState(Line matchingLine) {
            throw new IllegalStateException("Unable to parse " + matchingLine.getType().toString() + ": " + matchingLine.getRaw());
        }
    };

    private final LineType[] acceptableLineTypes;

    State(LineType... acceptableLineTypes) {
        this.acceptableLineTypes = acceptableLineTypes;
    }

    Line matchLine(String line) {
        for (LineType lineType : acceptableLineTypes) {
            Line match = lineType.match(line);
            if (match != null) return match;
        }
        return null;
    }

    protected abstract State nextState(Line matchingLine);
}

