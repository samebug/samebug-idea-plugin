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
package com.samebug.clients.idea.components.project;

import com.intellij.openapi.project.Project;
import com.samebug.clients.common.search.api.LogScanner;
import com.samebug.clients.common.search.api.LogScannerFactory;
import com.samebug.clients.common.search.api.StackTraceListener;
import com.samebug.clients.common.search.api.entities.tracking.DebugSessionInfo;
import com.samebug.clients.common.search.matcher.StackTraceMatcher;
import com.samebug.clients.idea.messages.model.StackTraceMatcherListener;

public class StackTraceMatcherFactory implements LogScannerFactory {
    private final StackTraceListener listener;

    public StackTraceMatcherFactory(Project project) {
        this.listener = new StackTracePublisher(project);
    }

    @Override
    public LogScanner createScanner(DebugSessionInfo sessionInfo) {
        return new StackTraceMatcher(listener, sessionInfo);
    }


    static private class StackTracePublisher implements StackTraceListener {
        private final Project project;

        StackTracePublisher(Project project) {
            this.project = project;
        }

        @Override
        public void stacktraceFound(DebugSessionInfo sessionInfo, String stacktrace) {
            if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceMatcherListener.TOPIC).stackTraceFound(project, sessionInfo, stacktrace);
        }
    }

}