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
package com.samebug.clients.idea.project.autosearch.console;

import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.project.autosearch.StackTraceMatcherFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RunDebugWatcher extends AbstractProjectComponent implements RunContentWithExecutorListener, Disposable {
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

    // RunContentWithExecutorListener overrides
    public void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        if (descriptor != null) {
            initListener(descriptor);
        }
    }

    public void contentRemoved(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        if (descriptor != null) {
            removeListener(descriptor);
        }
    }

    // Displosable overrides
    public void dispose() {
        for (Map.Entry<Integer, ConsoleScanner> entry : listeners.entrySet()) {
            entry.getValue().stop();
        }
        listeners.clear();
    }

    private synchronized ConsoleScanner initListener(@NotNull RunContentDescriptor descriptor) {
        Integer descriptorHashCode = System.identityHashCode(descriptor);
        ConsoleScanner existingScanner = listeners.get(descriptorHashCode);

        if (existingScanner != null) {
            return existingScanner;
        } else {
            return createScanner(descriptor, descriptorHashCode);
        }
    }

    private ConsoleScanner createScanner(@NotNull RunContentDescriptor descriptor, Integer descriptorHashCode) {
        if (descriptor.getProcessHandler() == null) return null;

        ConsoleScanner listener = new ConsoleScanner(scannerFactory, this, descriptor);
        listeners.put(descriptorHashCode, listener);
        descriptor.getProcessHandler().addProcessListener(listener);
        return listener;
    }

    synchronized void removeListener(RunContentDescriptor descriptor) {
        Integer descriptorHashCode = System.identityHashCode(descriptor);
        listeners.remove(descriptorHashCode);
    }

    private final Map<Integer, ConsoleScanner> listeners = new HashMap<Integer, ConsoleScanner>();
    private StackTraceMatcherFactory scannerFactory;


}
