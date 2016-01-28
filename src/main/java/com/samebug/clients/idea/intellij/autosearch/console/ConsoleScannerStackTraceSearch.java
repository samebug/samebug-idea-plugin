package com.samebug.clients.idea.intellij.autosearch.console;

import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.api.StackTraceListener;
import com.samebug.clients.idea.intellij.autosearch.StackTraceSearch;
import com.samebug.clients.idea.intellij.autosearch.StackTraceMatcherFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConsoleScannerStackTraceSearch implements StackTraceListener, RunContentWithExecutorListener {
    public ConsoleScannerStackTraceSearch(@NotNull StackTraceSearch stacktraceSearch, @NotNull Project project, StackTraceSearch.SearchResultListener searchResultListener) {
        this.stacktraceSearch = stacktraceSearch;
        this.searchResultListener = searchResultListener;

        this.logScannerFactory = new StackTraceMatcherFactory(this);
        this.scannerManager = new ScannerManager(logScannerFactory);
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(RunContentManager.TOPIC, this);
    }

    public void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        if (descriptor != null) {
            scannerManager.initListener(descriptor);
        }
    }

    public void contentRemoved(@Nullable RunContentDescriptor descriptor, @NotNull com.intellij.execution.Executor executor) {
        if (descriptor != null) {
            scannerManager.removeListener(descriptor);
        }
    }

    private final static Logger logger = Logger.getInstance(ConsoleScannerStackTraceSearch.class);

    @Override
    public void stacktraceFound(String stacktrace) {
        stacktraceSearch.search(stacktrace, searchResultListener);
    }

    private final ScannerManager scannerManager;
    private final StackTraceSearch stacktraceSearch;
    private final StackTraceSearch.SearchResultListener searchResultListener;
    private final StackTraceMatcherFactory logScannerFactory;
}
