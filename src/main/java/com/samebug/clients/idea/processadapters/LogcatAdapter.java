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
package com.samebug.clients.idea.processadapters;

import com.android.ddmlib.MultiLineReceiver;
import com.samebug.clients.search.api.LogScanner;

/**
 * Receives the output of the logcat process.
 * <p/>
 * The logcat output is forwarded to a LogScanner.
 */
public class LogcatAdapter extends MultiLineReceiver {

    private final LogScanner logScanner;
    private boolean finished = false;

    public LogcatAdapter(LogScanner logScanner) {
        this.logScanner = logScanner;

    }

    @Override
    public void processNewLines(String[] lines) {
        for (String line : lines) {
            logScanner.line(line + "\n");
        }
    }

    public void finish() {
        finished = true;
        flush();
        logScanner.end();
        done();
    }

    @Override
    public boolean isCancelled() {
        return finished;
    }
}
