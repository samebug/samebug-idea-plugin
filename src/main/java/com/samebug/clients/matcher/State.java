package com.samebug.clients.matcher;

import static com.samebug.clients.matcher.LineType.*;

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

