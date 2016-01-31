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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.samebug.clients.matcher.State.ExceptionStartedWithMessage;
import static com.samebug.clients.matcher.State.StackTraceStarted;
import static com.samebug.clients.matcher.State.WaitingForExceptionStart;

abstract class MatcherStateMachine {
    protected abstract void stackTraceFound();

    protected abstract void matchingFailed();

    public MatcherStateMachine() {
        this(WaitingForExceptionStart, new ArrayList<Line>());
    }

    public MatcherStateMachine step(String line) {
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

    public void stop() {
        switch (state) {
            case StackTraceStarted:
            case More:
                stackTraceFound();
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

    private MatcherStateMachine(State state, ArrayList<Line> lines) {
        this.state = state;
        this.lines = lines;
    }

    private MatcherStateMachine transition(@NotNull State nextState, @NotNull Line line) {
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

    protected String getStackTrace() {
        StringBuilder b = new StringBuilder();
        boolean first = true;
        for (Line line : lines) {
            if (!first) b.append("\n");
            else first = false;
            b.append(line.getRaw());
        }
        return b.toString();
    }

    public MatcherStateMachine reset() {
        lines.clear();
        state = WaitingForExceptionStart;
        return this;
    }

    private State state;
    protected final ArrayList<Line> lines;
    private int nonChangingSteps = 0;
}
