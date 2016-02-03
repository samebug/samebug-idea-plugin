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
package com.samebug.clients.idea.scanners;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.util.Key;
import com.samebug.clients.search.api.LogScanner;
import com.samebug.clients.search.api.LogScannerFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Scans running process outputs
 */
public class ConsoleScanner extends ProcessAdapter {
    public ConsoleScanner(@NotNull LogScannerFactory scannerFactory) {
        this.scannerFactory = scannerFactory;
    }

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {
        String line = event.getText();
        getOrCreateScanner(outputType).line(line);
    }

    @Override
    public void processTerminated(ProcessEvent event) {
        stop();
    }

    public void stop() {
        stopScanners();
    }

    private synchronized LogScanner getOrCreateScanner(Key outputType) {
        LogScanner scanner = scanners.get(outputType);
        if (scanner == null) {
            scanner = scannerFactory.createScanner();
            scanners.put(outputType, scanner);
        }
        return scanner;
    }

    private synchronized void stopScanners() {
        for (Key outputType : new LinkedList<Key>(scanners.keySet())) {
            LogScanner scanner = scanners.get(outputType);
            if (scanner != null) {
                scanner.end();
                scanners.remove(outputType);
            }
        }
    }

    private final Map<Key, LogScanner> scanners = new HashMap<Key, LogScanner>();
    private final LogScannerFactory scannerFactory;
}
