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
package com.samebug.clients.idea.intellij.autosearch.android;

import com.android.ddmlib.MultiLineReceiver;
import com.samebug.clients.api.LogScanner;

import java.io.*;

/**
 * Scans running process outputs
 */
class OutputScanner extends MultiLineReceiver {

    private final LogScanner logScanner;

    public OutputScanner(LogScanner logScanner) throws IOException {
        this.logScanner = logScanner;

    }

    @Override
    public void processNewLines(String[] lines) {
        for (String line: lines) {
            logScanner.line(line + "\n");
        }
    }

    public void finish() {
        flush();
        logScanner.end();
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
