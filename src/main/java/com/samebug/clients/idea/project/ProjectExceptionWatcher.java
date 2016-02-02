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
package com.samebug.clients.idea.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.project.autosearch.StackTraceSearch;
import com.samebug.clients.idea.project.autosearch.android.LogcatScannerManager;
import com.samebug.clients.idea.project.autosearch.console.ConsoleScannerManager;
import com.samebug.clients.idea.project.autosearch.StackTraceMessageListener;
import org.jetbrains.annotations.NotNull;

public class ProjectExceptionWatcher implements ProjectComponent {

    private SearchResultNotifier searchResultNotifier;
    private StackTraceSearch stackTraceSearch;
    private ConsoleScannerManager consoleScannerManager;
    private LogcatScannerManager logcatScannerManager;

    private ProjectExceptionWatcher(final Project project) {
        this.project = project;
    }

    /**
     * Subscribe a @see com.samebug.clients.idea.intellij.ProcessOutputScannerInstaller on the project message bus
     */
    @Override
    public void projectOpened() {
        initScanners(project);
    }


    @Override
    public void initComponent() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                IdeaSamebugPlugin.initIfNeeded();
            }
        });
    }

    private void initScanners(@NotNull final Project project) {
        this.stackTraceSearch = new StackTraceSearch(project, IdeaSamebugPlugin.getClient());
        this.searchResultNotifier = new SearchResultNotifier(project);
        this.consoleScannerManager = new ConsoleScannerManager(project);
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ProjectExceptionWatcher.this.logcatScannerManager = LogcatScannerManager.createManagerForAndroidProject(project);
            }
        });
        project.getMessageBus().connect().subscribe(StackTraceMessageListener.FOUND_TOPIC, new StackTraceMessageListener() {
            @Override
            public void stackTraceFound(String stackTrace) {
                stackTraceSearch.search((String) stackTrace);
            }
        });
    }


    @Override
    public void disposeComponent() {
        consoleScannerManager.dispose();
        if (logcatScannerManager != null) {
            logcatScannerManager.dispose();
        }
    }

    @Override
    @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }


    @Override
    public void projectClosed() {
    }

    private final Project project;
}

