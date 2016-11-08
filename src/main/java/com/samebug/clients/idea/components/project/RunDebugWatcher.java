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

import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.processadapters.RunDebugAdapter;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.search.api.entities.tracking.DebugSessionInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RunDebugWatcher extends AbstractProjectComponent implements RunContentWithExecutorListener {
    public RunDebugWatcher(Project project) {
        super(project);
    }

    // ProjectComponent overrides
    @Override
    public void projectOpened() {
        this.scannerFactory = new StackTraceMatcherFactory(myProject);
        MessageBusConnection messageBusConnection = myProject.getMessageBus().connect();
        messageBusConnection.subscribe(RunContentManager.TOPIC, this);
    }

    @Override
    public void projectClosed() {
        for (RunDebugAdapter listener : listeners.values()) {
            listener.stop();
        }
        listeners.clear();
    }

    // RunContentWithExecutorListener overrides
    public void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        myProject.getComponent(ToolWindowController.class).changeToolwindowIcon(false);
        if (descriptor != null) {
            initListener(descriptor);
        }
    }

    public void contentRemoved(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        if (descriptor != null) {
            removeListener(descriptor);
        }
    }

    // implementation
    private synchronized RunDebugAdapter initListener(@NotNull RunContentDescriptor descriptor) {
        Integer descriptorHashCode = System.identityHashCode(descriptor);
        RunDebugAdapter existingScanner = listeners.get(descriptorHashCode);

        if (existingScanner != null) {
            return existingScanner;
        } else {
            return createScanner(descriptor, descriptorHashCode);
        }
    }

    private RunDebugAdapter createScanner(@NotNull RunContentDescriptor descriptor, Integer descriptorHashCode) {
        if (descriptor.getProcessHandler() == null) return null;

        DebugSessionInfo sessionInfo = new DebugSessionInfo("run/debug");
        Tracking.projectTracking(myProject).trace(Events.debugStart(myProject, sessionInfo));
        RunDebugAdapter listener = new RunDebugAdapter(scannerFactory, sessionInfo);
        listeners.put(descriptorHashCode, listener);
        debugSessionIds.put(descriptorHashCode, sessionInfo);
        descriptor.getProcessHandler().addProcessListener(listener);
        return listener;
    }

    synchronized void removeListener(RunContentDescriptor descriptor) {
        Integer descriptorHashCode = System.identityHashCode(descriptor);
        Tracking.projectTracking(myProject).trace(Events.debugStop(myProject, debugSessionIds.get(descriptorHashCode)));
        debugSessionIds.remove(descriptorHashCode);
        listeners.remove(descriptorHashCode);
    }

    private final Map<Integer, RunDebugAdapter> listeners = new HashMap<Integer, RunDebugAdapter>();
    private final Map<Integer, DebugSessionInfo> debugSessionIds = new HashMap<Integer, DebugSessionInfo>();
    private StackTraceMatcherFactory scannerFactory;
}
