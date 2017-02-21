/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.components.project;

import com.intellij.execution.console.DuplexConsoleView;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.ui.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.DebugSessionInfo;
import com.samebug.clients.common.search.api.LogScannerFactory;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.search.StackTraceMatcherFactory;
import com.samebug.clients.idea.search.console.ConsoleWatcher;
import com.samebug.clients.idea.search.processadapters.RunDebugAdapter;
import com.samebug.clients.idea.tracking.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RunDebugWatcher implements RunContentWithExecutorListener, Disposable {
    private final Project myProject;

    public RunDebugWatcher(Project project) {
        this.myProject = project;
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(this);
        messageBusConnection.subscribe(RunContentManager.TOPIC, this);
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
            listeners.remove(descriptorHashCode);
            DebugSessionInfo sessionInfo = debugSessionIds.get(descriptorHashCode);
            if (sessionInfo != null) {
                Tracking.projectTracking(myProject).trace(Events.debugStop(myProject, sessionInfo));
                debugSessionIds.remove(descriptorHashCode);
                IdeaSamebugPlugin.getInstance().getSearchRequestStore().removeSession(sessionInfo);
            }
        }
    }

    private void createListener(@NotNull RunContentDescriptor descriptor, Integer descriptorHashCode) {
        // TODO do something meaningfule when starting a run/debug session?
//        ApplicationManager.getApplication().invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                ToolWindowController twc = myProject.getComponent(SamebugProjectComponent.class).getToolWindowController();
//                twc.changeToolwindowIcon(false);
//            }
//        });

        DebugSessionInfo sessionInfo = new DebugSessionInfo("run/debug");

        ProcessHandler processHandler = descriptor.getProcessHandler();
        if (processHandler != null) {
            ExecutionConsole console = descriptor.getExecutionConsole();
            if (console instanceof ConsoleView) {
                ConsoleViewImpl impl = extractConsoleImpl((ConsoleView) console);
                if (impl != null) {
                    // do we have to keep this reference?
                    new ConsoleWatcher(myProject, impl, sessionInfo);
                }
            }
            final LogScannerFactory scannerFactory = new StackTraceMatcherFactory(myProject, sessionInfo);
            RunDebugAdapter listener = new RunDebugAdapter(scannerFactory);
            listeners.put(descriptorHashCode, listener);
            debugSessionIds.put(descriptorHashCode, sessionInfo);
            processHandler.addProcessListener(listener);

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

    @Override
    public void dispose() {
        for (RunDebugAdapter listener : listeners.values()) {
            listener.stop();
        }
        listeners.clear();
    }
}
