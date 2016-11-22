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

import com.intellij.execution.console.DuplexConsoleView;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.ui.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.LogScannerFactory;
import com.samebug.clients.common.search.api.entities.tracking.DebugSessionInfo;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.console.ConsoleWatcher;
import com.samebug.clients.idea.processadapters.RunDebugAdapter;
import com.samebug.clients.idea.tracking.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RunDebugWatcher extends AbstractProjectComponent implements RunContentWithExecutorListener {
    public RunDebugWatcher(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
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

    public synchronized void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        Integer descriptorHashCode = System.identityHashCode(descriptor);
        if (listeners.get(descriptorHashCode) == null && descriptor != null) {
            createListener(descriptor, descriptorHashCode);
        }
    }

    public synchronized void contentRemoved(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        if (descriptor != null) {
            Integer descriptorHashCode = System.identityHashCode(descriptor);
            Tracking.projectTracking(myProject).trace(Events.debugStop(myProject, debugSessionIds.get(descriptorHashCode)));

            DebugSessionInfo sessionInfo = debugSessionIds.get(descriptorHashCode);
            debugSessionIds.remove(descriptorHashCode);
            listeners.remove(descriptorHashCode);
            myProject.getComponent(SamebugProjectComponent.class).getSessionService().removeSession(sessionInfo);
        }
    }

    private void createListener(@NotNull RunContentDescriptor descriptor, Integer descriptorHashCode) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                myProject.getComponent(ToolWindowController.class).changeToolwindowIcon(false);
            }
        });

        DebugSessionInfo sessionInfo = new DebugSessionInfo("run/debug");

        if (descriptor.getProcessHandler() != null) {
            ExecutionConsole console = descriptor.getExecutionConsole();
            if (console instanceof ConsoleView) {
                ConsoleViewImpl impl = extractConsoleImpl((ConsoleView) console);
                if (impl != null) {
                    // do we have to keep this reference?
                    new ConsoleWatcher(impl, sessionInfo);
                }
            }
            final LogScannerFactory scannerFactory = new StackTraceMatcherFactory(myProject, sessionInfo);
            RunDebugAdapter listener = new RunDebugAdapter(scannerFactory);
            listeners.put(descriptorHashCode, listener);
            debugSessionIds.put(descriptorHashCode, sessionInfo);
            descriptor.getProcessHandler().addProcessListener(listener);

            Tracking.projectTracking(myProject).trace(Events.debugStart(myProject, sessionInfo));
        }
    }

    @Nullable
    private static ConsoleViewImpl extractConsoleImpl(ConsoleView console) {
        ConsoleViewImpl impl;

        if (console instanceof ConsoleViewImpl) {
            impl = (ConsoleViewImpl) console;
        } else if (console instanceof DuplexConsoleView) {
            impl = extractConsoleImpl(((DuplexConsoleView) console).getPrimaryConsoleView());
            if (impl == null) impl = extractConsoleImpl(((DuplexConsoleView) console).getSecondaryConsoleView());
        } else if (console instanceof BaseTestsOutputConsoleView) {
            impl = extractConsoleImpl(((BaseTestsOutputConsoleView) console).getConsole());
        } else {
            impl = null;
        }
        return impl;
    }


    private final Map<Integer, RunDebugAdapter> listeners = new HashMap<Integer, RunDebugAdapter>();
    private final Map<Integer, DebugSessionInfo> debugSessionIds = new HashMap<Integer, DebugSessionInfo>();
}
