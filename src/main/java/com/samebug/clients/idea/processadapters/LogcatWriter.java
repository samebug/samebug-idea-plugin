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

import com.android.tools.idea.logcat.AndroidConsoleWriter;
import com.android.tools.idea.logcat.AndroidLogcatFormatter;
import com.android.tools.idea.logcat.AndroidLogcatPreferences;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.search.api.LogScanner;
import org.jetbrains.annotations.NotNull;

public class LogcatWriter implements AndroidConsoleWriter {
    private final AndroidLogcatFormatter logFormatter;
    private final LogScanner logScanner;

    public LogcatWriter(Project project, LogScanner logScanner) {
        AndroidLogcatPreferences logcatPreferences = AndroidLogcatPreferences.getInstance(project);
        this.logFormatter = new AndroidLogcatFormatter(logcatPreferences);
        this.logScanner = logScanner;
    }

    @Override
    public void clear() {

    }

    @Override
    public void addMessage(@NotNull String text) {
        String formattedMessage = logFormatter.formatMessage(text);
        logScanner.append(formattedMessage + "\n");
    }
}
