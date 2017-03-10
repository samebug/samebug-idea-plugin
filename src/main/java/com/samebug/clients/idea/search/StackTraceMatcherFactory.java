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
package com.samebug.clients.idea.search;

import com.intellij.openapi.project.Project;
import com.samebug.clients.common.entities.search.DebugSessionInfo;
import com.samebug.clients.common.search.api.LogScanner;
import com.samebug.clients.common.search.api.LogScannerFactory;
import com.samebug.clients.common.search.api.StackTraceListener;
import com.samebug.clients.common.search.matcher.StackTraceMatcher;

public class StackTraceMatcherFactory implements LogScannerFactory {
    private final Project myProject;
    private final StackTraceListener listener;
    private final DebugSessionInfo sessionInfo;

    public StackTraceMatcherFactory(Project project, DebugSessionInfo sessionInfo) {
        myProject = project;
        this.listener = new StackTracePublisher();
        this.sessionInfo = sessionInfo;
    }

    @Override
    public LogScanner createScanner() {
        return new StackTraceMatcher(listener);
    }


    private class StackTracePublisher implements StackTraceListener {
        @Override
        public void stacktraceFound(String stacktrace) {
            if (!myProject.isDisposed()) myProject.getMessageBus().syncPublisher(StackTraceMatcherListener.TOPIC).stackTraceFound(myProject, sessionInfo, stacktrace);
        }
    }

}