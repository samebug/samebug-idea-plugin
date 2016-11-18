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
package com.samebug.clients.idea.console;

import com.intellij.execution.Executor;
import com.intellij.execution.console.DuplexConsoleView;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.DumbAware;
import com.samebug.clients.common.entities.search.Saved;
import com.samebug.clients.idea.messages.view.FocusListener;
import com.samebug.clients.idea.resources.SamebugIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RunWatcher implements RunContentWithExecutorListener {
    private final Logger LOGGER = Logger.getInstance(RunWatcher.class);
    private final Map<ProcessHandler, ConsoleWatcher> consoles;

    public RunWatcher() {
        consoles = new ConcurrentHashMap<ProcessHandler, ConsoleWatcher>();
    }

    @Override
    public void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull Executor executor) {
        if (descriptor != null && descriptor.getProcessHandler() != null) {
            // TODO this is really fragile as it won't work on custom console implementations
            // TODO bookkeep the RunDebugWatchers, so we can know to which process does a trace belong
            ExecutionConsole console = descriptor.getExecutionConsole();

            if (console instanceof ConsoleView) {
                ConsoleViewImpl impl = extractConsoleImpl((ConsoleView) console);
                if (impl != null) {
                    ConsoleWatcher watcher = new ConsoleWatcher(impl);
                    consoles.put(descriptor.getProcessHandler(), watcher);
                }
            }
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

    @Override
    public void contentRemoved(@Nullable RunContentDescriptor descriptor, @NotNull Executor executor) {
        if (descriptor != null) {
            consoles.remove(descriptor.getProcessHandler());
        }
    }
}

class SavedSearchMark extends GutterIconRenderer implements DumbAware {
    private final Saved search;

    public SavedSearchMark(Saved search) {
        this.search = search;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return SamebugIcons.gutterSamebug;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SavedSearchMark) {
            SavedSearchMark rhs = (SavedSearchMark) o;
            return rhs.search.equals(search);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return search.hashCode();
    }

    @Override
    public boolean isNavigateAction() {
        return true;
    }

    @Override
    @NotNull
    public String getTooltipText() {
        return "Search " + search.getSavedSearch().getSearchId()
                + "\nClick to show solutions.";
    }

    @NotNull
    public AnAction getClickAction() {
        return new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                getEventProject(e).getMessageBus().syncPublisher(FocusListener.TOPIC).focusOnSearch(search.getSavedSearch().getSearchId());
            }
        };
    }

}

