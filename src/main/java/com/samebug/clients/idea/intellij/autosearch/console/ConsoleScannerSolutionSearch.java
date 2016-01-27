package com.samebug.clients.idea.intellij.autosearch.console;

import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.SamebugProjectComponent;
import com.samebug.clients.idea.intellij.autosearch.AutomatedSolutionSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConsoleScannerSolutionSearch extends AutomatedSolutionSearch implements RunContentWithExecutorListener {
    private final ConsoleScannerManager consoleScannerManager;

    public ConsoleScannerSolutionSearch(Project project) {
        super(project);

        this.consoleScannerManager = new ConsoleScannerManager(logScannerFactory);
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(project);
        messageBusConnection.subscribe(RunContentManager.TOPIC, this);
    }

    public void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        if (descriptor != null) {
            consoleScannerManager.initListener(descriptor);
        }
    }

    public void contentRemoved(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        if (descriptor != null) {
            consoleScannerManager.removeListener(descriptor);
        }
    }

    private final static Logger logger = Logger.getInstance(SamebugProjectComponent.class);
}
