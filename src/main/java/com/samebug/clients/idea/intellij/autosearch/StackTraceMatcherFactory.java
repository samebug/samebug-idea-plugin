package com.samebug.clients.idea.intellij.autosearch;

import com.samebug.clients.api.LogScanner;
import com.samebug.clients.api.LogScannerFactory;
import com.samebug.clients.api.StackTraceListener;
import com.samebug.clients.matcher.StackTraceMatcher;

public class StackTraceMatcherFactory implements LogScannerFactory {
    private StackTraceListener listener;

    public StackTraceMatcherFactory(StackTraceListener listener) {
        this.listener = listener;
    }

    @Override
    public LogScanner createScanner() {
        return new StackTraceMatcher(listener);
    }
}
