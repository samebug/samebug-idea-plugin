package com.samebug.clients.idea.intellij.autosearch.android;

import com.android.ddmlib.*;
import com.intellij.execution.Executor;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.api.StackTraceListener;
import com.samebug.clients.idea.intellij.autosearch.StackTraceSearch;
import com.samebug.clients.idea.intellij.autosearch.StackTraceMatcherFactory;
import com.samebug.clients.idea.intellij.autosearch.android.exceptions.UnableToCreateReceiver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AndroidShellSolutionSearch implements StackTraceListener, AndroidDebugBridge.IDeviceChangeListener, Disposable, RunContentWithExecutorListener {

    public AndroidShellSolutionSearch(StackTraceSearch stacktraceSearch, StackTraceSearch.SearchResultListener searchResultListener) {
        super();
        this.stacktraceSearch = stacktraceSearch;
        this.searchResultListener = searchResultListener;
        this.logScannerFactory = new StackTraceMatcherFactory(this);
        this.outputScannerManager = new ScannerManager(logScannerFactory);
        initialize();
    }


    @Override
    public void deviceConnected(IDevice device) {
        try {
            if (device.isOnline()) {
                outputScannerManager.initReceiver(device);
            }
        } catch (UnableToCreateReceiver e) {
            logger.error("Unable to connect device", e);
        }
    }

    @Override
    public void deviceDisconnected(IDevice device) {
        outputScannerManager.removeReceiver(device);
    }

    @Override
    public void deviceChanged(IDevice device, int changeMask) {
        try {
            if (device.isOnline()) {
                outputScannerManager.initReceiver(device);
            }
        } catch (UnableToCreateReceiver e) {
            logger.error("Unable to connect device", e);
        }
    }

    private void initialize() {
        AndroidDebugBridge.initIfNeeded(false);
        AndroidDebugBridge bridge = AndroidDebugBridge.getBridge();
        if (bridge == null) {
            bridge = AndroidDebugBridge.createBridge();
        }
        if (bridge.isConnected()) {
            AndroidDebugBridge.addDeviceChangeListener(this);
            for (IDevice device: bridge.getDevices()) {
                deviceConnected(device);
            }
            initialized = true;
        } else {
            initialized = false;
        }
    }

    private final static Logger logger = Logger.getInstance(AndroidShellSolutionSearch.class);
    private final StackTraceMatcherFactory logScannerFactory;
    private final ScannerManager outputScannerManager;
    private volatile boolean initialized;
    private final StackTraceSearch stacktraceSearch;
    private final StackTraceSearch.SearchResultListener searchResultListener;

    @Override
    public void dispose() {
        AndroidDebugBridge.terminate();
    }

    @Override
    public void contentSelected(@Nullable RunContentDescriptor runContentDescriptor, @NotNull Executor executor) {
        if (!initialized) initialize();
    }

    @Override
    public void contentRemoved(@Nullable RunContentDescriptor runContentDescriptor, @NotNull Executor executor) {

    }

    @Override
    public void stacktraceFound(String stacktrace) {
        stacktraceSearch.search(stacktrace, searchResultListener);
    }
}
