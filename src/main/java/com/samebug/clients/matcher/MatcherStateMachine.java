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

import java.util.ArrayList;

import static com.samebug.clients.matcher.State.*;

abstract class MatcherStateMachine {
    protected abstract void stackTraceFound();

    protected abstract void matchingFailed();

    MatcherStateMachine() {
        this(new ArrayList<Line>());
    }

    MatcherStateMachine step(String line) {
        Line matchingLine = state.matchLine(line);
        State nextState;
        if (matchingLine == null) {
            matchingFailed();
            return restart(line);
        } else {
            nextState = state.nextState(matchingLine);
            if (state == ExceptionStartedWithMessage && nonChangingSteps > 10) {
                return restart(line);
            } else if (state == StackTraceStarted && nonChangingSteps > 500) {
                return restart(line);
            } else {
                return transition(nextState, matchingLine);
            }
        }
    }

    void stop() {
        switch (state) {
            case StackTraceStarted:
            case More:
                stackTraceFound();
            default:
        }
    }


    private MatcherStateMachine restart(String line) {
        state = WaitingForExceptionStart;
        Line matchingLine = state.matchLine(line);
        if (matchingLine == null) {
            return reset();
        } else {
            State nextState = state.nextState(matchingLine);
            return transition(nextState, matchingLine);
        }
    }

    private MatcherStateMachine(ArrayList<Line> lines) {
        this.state = State.WaitingForExceptionStart;
        this.lines = lines;
    }

    private MatcherStateMachine transition(@Nonnull State nextState, @Nonnull Line line) {
        if (state == nextState) {
            nonChangingSteps++;
        } else {
            nonChangingSteps = 0;
        }
        switch (nextState) {
            case ExceptionFinished:
                stackTraceFound();
                state = WaitingForExceptionStart;
                return restart(line.getRaw());
            default:
                lines.add(line);
                state = nextState;
                return this;
        }
    }

    String getStackTrace() {
        StringBuilder b = new StringBuilder();
        boolean first = true;
        for (Line line : lines) {
            if (!first) b.append("\n");
            else first = false;
            b.append(line.getRaw());
        }
        return b.toString();
    }

    private MatcherStateMachine reset() {
        lines.clear();
        state = WaitingForExceptionStart;
        return this;
    }

    private State state;
    private final ArrayList<Line> lines;
    private int nonChangingSteps = 0;
}
