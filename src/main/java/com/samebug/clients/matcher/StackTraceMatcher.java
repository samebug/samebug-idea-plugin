package com.samebug.clients.matcher;

import com.samebug.clients.api.LogScanner;
import com.samebug.clients.api.StackTraceListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.samebug.clients.matcher.State.*;

public class StackTraceMatcher extends MatcherStateMachine implements LogScanner {
    private final StackTraceListener listener;

    public StackTraceMatcher(StackTraceListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public void line(String line) {
        step(line);
    }

    @Override
    public void end() {
        stop();
    }

    Line recognize(String line) {
        for (LineType lineType : LineType.values()) {
            Line match = lineType.match(line);
            if (match != null) return match;
        }
        return null;
    }

    @Override
    protected void stackTraceFound() {
        listener.stacktraceFound(getStackTrace());
    }

    @Override
    protected void matchingFailed() {

    }
}

