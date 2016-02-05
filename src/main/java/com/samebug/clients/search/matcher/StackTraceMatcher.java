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

/**
 * Scans the log for stacktrace.
 *
 * When a stacktrace is found, notifies the StackTraceListener.
 */
public class StackTraceMatcher extends MatcherStateMachine implements LogScanner {
    private final StackTraceListener listener;

    public StackTraceMatcher(StackTraceListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public void line(String line) {
        step(line);
    }

    @Override
    public void end() {
        stop();
    }

    Line recognize(String line) {
        for (LineType lineType : LineType.values()) {
            Line match = lineType.match(line);
            if (match != null) return match;
        }
        return null;
    }

    @Override
    protected void stackTraceFound() {
        listener.stacktraceFound(getStackTrace());
    }

    @Override
    protected void matchingFailed() {

    }
}

