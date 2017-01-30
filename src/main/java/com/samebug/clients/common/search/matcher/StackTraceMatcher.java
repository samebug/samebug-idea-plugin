/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.common.search.matcher;

import com.samebug.clients.common.search.api.LogScanner;
import com.samebug.clients.common.search.api.StackTraceListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Scans the log for stacktrace.
 * <p/>
 * When a stacktrace is found, notifies the StackTraceListener.
 */
final public class StackTraceMatcher extends MatcherStateMachine implements LogScanner {
    public static final int FINISH_STACKTRACE_TIMEOUT = 1000;
    private final StackTraceListener listener;
    // FIXME: It would be better not to use swing timer here
    private Timer timer;
    private final StringBuilder lineBuffer;


    public StackTraceMatcher(StackTraceListener listener) {
        super();
        this.listener = listener;
        this.timer = new Timer(FINISH_STACKTRACE_TIMEOUT, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StackTraceMatcher.this.end();
            }
        });
        this.timer.setRepeats(false);
        this.lineBuffer = new StringBuilder();
    }

    @Override
    public void append(String text) {
        timer.stop();
        String[] lines = text.split("\\r\\n|\\r|\\n", -1);
        if (lines.length == 1) {
            lineBuffer.append(lines[0]);
        } else {
            lineBuffer.append(lines[0]);
            processLine(lineBuffer.toString());
            lineBuffer.setLength(0);
            for (int i = 1; i < lines.length - 1; ++i) {
                processLine(lines[i]);
            }
            lineBuffer.append(lines[lines.length - 1]);
        }

        timer.restart();
    }

    @Override
    public void end() {
        processLine(lineBuffer.toString());
        lineBuffer.setLength(0);
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
        listener.stacktraceFound(b.toString());
    }

    private void processLine(String line) {
        if (TeamCityDecoder.isTestFrameworkException(line)) {
            for (String processedLine : TeamCityDecoder.testFailureLines(line)) {
                step(processedLine);
            }
        } else step(line);

    }

}

