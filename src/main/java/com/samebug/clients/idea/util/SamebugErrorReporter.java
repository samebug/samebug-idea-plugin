package com.samebug.clients.idea.util;

import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import com.samebug.notifier.Samebug;
import com.samebug.notifier.core.exceptions.NotifierException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by poroszd on 2/9/16.
 */
public class SamebugErrorReporter extends ErrorReportSubmitter {

    public SamebugErrorReporter() {
        super();
        Samebug.init("1b789d3b-5ddf-42b2-a6fb-6a474bdc5f95");
    }

    @Override
    public String getReportActionText() {
        return null;
    }

    @Override
    public boolean submit(@NotNull IdeaLoggingEvent[] events,
                          @Nullable String additionalInfo,
                          @NotNull Component parentComponent,
                          @NotNull Consumer<SubmittedReportInfo> consumer) {
        try {
            for (IdeaLoggingEvent e : events) {
                Samebug.notify(e.getMessage(), e.getThrowable());
            }
            return true;
        } catch (NotifierException ex) {
            return false;
        }
    }

    @Override
    public boolean trySubmitAsync(IdeaLoggingEvent[] events, String info, Component parent, Consumer<SubmittedReportInfo> consumer) {
        return true;
    }

    @Override
    public void submitAsync(IdeaLoggingEvent[] events, String info, Component parent, Consumer<SubmittedReportInfo> consumer) {
        throw new Error("");
    }

    @Override
    public SubmittedReportInfo submit(IdeaLoggingEvent[] events, Component parent) {
        throw new Error("");
    }

}
