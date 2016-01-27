package com.samebug.clients.idea.intellij.autosearch.console;

import com.intellij.execution.ui.RunContentDescriptor;
import com.samebug.clients.api.LogScannerFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Installs a ProcessOutputScanner on Run/Debug Process standard and error outputs
 */
public class ConsoleScannerManager {

    public ConsoleScannerManager(@NotNull LogScannerFactory scannerFactory) {
        this.scannerFactory = scannerFactory;
    }

    public synchronized ConsoleScanner initListener(@NotNull RunContentDescriptor descriptor) {
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

    private final LogScannerFactory scannerFactory;
    private final Map<Integer, ConsoleScanner> listeners = new HashMap<Integer, ConsoleScanner>();
}

