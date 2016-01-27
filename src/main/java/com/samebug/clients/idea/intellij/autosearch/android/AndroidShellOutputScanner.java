package com.samebug.clients.idea.intellij.autosearch.android;

import com.android.ddmlib.MultiLineReceiver;
import com.samebug.clients.api.LogScanner;

import java.io.*;

/**
 * Scans running process outputs
 */
class AndroidShellOutputScanner extends MultiLineReceiver {

    private final LogScanner logScanner;

    public AndroidShellOutputScanner(LogScanner logScanner) throws IOException {
        this.logScanner = logScanner;

    }

    @Override
    public void processNewLines(String[] lines) {
        for (String line: lines) {
            logScanner.line(line + "\n");
        }
    }

    public void finish() {
        flush();
        logScanner.end();
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
