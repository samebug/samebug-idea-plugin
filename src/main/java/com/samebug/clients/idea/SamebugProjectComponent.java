/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.api.StackTraceListener;
import com.samebug.clients.idea.intellij.autosearch.StackTraceSearch;
import com.samebug.clients.idea.intellij.autosearch.android.LogcatScannerManager;
import com.samebug.clients.idea.intellij.autosearch.console.ConsoleScannerManager;

import javax.annotation.Nonnull;

public class SamebugProjectComponent implements ProjectComponent, StackTraceListener {

    private SearchResultNotifier searchResultNotifier;
    private StackTraceSearch stackTraceSearch;

    private SamebugProjectComponent(final Project project) {
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
        SamebugIdeaPlugin.initIfNeeded();
    }

    private void initScanners(@Nonnull final Project project) {

        this.stackTraceSearch = SamebugIdeaPlugin.getStackTraceSearch();
        this.searchResultNotifier = new SearchResultNotifier(project);
        ConsoleScannerManager consoleScannerManager = new ConsoleScannerManager(project, this);
        LogcatScannerManager logcatScannerManager = LogcatScannerManager.createManagerForAndroidProject(project, this);
    }


    @Override
    public void disposeComponent() {
    }

    @Override
    @Nonnull
    public String getComponentName() {
        return getClass().getSimpleName();
    }


    @Override
    public void projectClosed() {
    }

    private final Project project;


    private final static Logger LOGGER = Logger.getInstance(SamebugProjectComponent.class);


    @Override
    public void stacktraceFound(String stacktrace) {
        stackTraceSearch.search(stacktrace, searchResultNotifier);
    }
}

