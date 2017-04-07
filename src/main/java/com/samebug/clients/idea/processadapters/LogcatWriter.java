/*
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.processadapters;

import com.android.ddmlib.logcat.LogCatHeader;
import com.android.ddmlib.logcat.LogCatMessage;
import com.android.tools.idea.logcat.AndroidLogcatFormatter;
import com.android.tools.idea.logcat.AndroidLogcatPreferences;
import com.android.tools.idea.logcat.AndroidLogcatService;
import com.samebug.clients.common.search.LogScanner;
import org.jetbrains.annotations.NotNull;

public class LogcatWriter extends FormattedLogLineReceiver {
    private final LogScanner logScanner;
    private final AndroidLogcatFormatter logFormatter;

    public LogcatWriter(AndroidLogcatPreferences logcatPreferences, LogScanner logScanner) {
        this.logScanner = logScanner;
        this.logFormatter = new AndroidLogcatFormatter(logcatPreferences);
    }

    @Override
    protected void receiveFormattedLogLine(@NotNull String message) {
        String formattedMessage = logFormatter.formatMessage(message);
        logScanner.append(formattedMessage + "\n");
    }
}

/**
 * We have to imitate the behavior of the logcat console, but that class was not public.
 * <p>
 * This is definitely not the best solution, but works for now.
 */
abstract class FormattedLogLineReceiver implements AndroidLogcatService.LogcatListener {
    private LogCatHeader myActiveHeader;

    FormattedLogLineReceiver() {
    }

    @Override
    public final void onLogLineReceived(@NotNull LogCatMessage line) {
        String message;
        if (!line.getHeader().equals(this.myActiveHeader)) {
            this.myActiveHeader = line.getHeader();
            message = AndroidLogcatFormatter.formatMessageFull(this.myActiveHeader, line.getMessage());
            this.receiveFormattedLogLine(message);
        } else {
            message = AndroidLogcatFormatter.formatContinuation(line.getMessage());
            this.receiveFormattedLogLine(message);
        }

    }

    protected abstract void receiveFormattedLogLine(@NotNull String var1);
}
