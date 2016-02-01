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
package com.samebug.clients.idea.intellij.autosearch.console;

import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.api.StackTraceListener;
import com.samebug.clients.idea.intellij.autosearch.StackTraceMatcherFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ConsoleScannerManager implements RunContentWithExecutorListener, Disposable {
    public ConsoleScannerManager(Project project, StackTraceListener stackTraceListener) {

        this.scannerFactory = new StackTraceMatcherFactory(stackTraceListener);
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(RunContentManager.TOPIC, this);
    }

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
    private final StackTraceMatcherFactory scannerFactory;

    public void dispose() {
       for (Map.Entry<Integer, ConsoleScanner> entry :  listeners.entrySet()) {
           entry.getValue().stop();
       }
        listeners.clear();
    }
}
