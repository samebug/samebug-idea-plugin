package com.samebug.clients.idea.ui;

import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.tracking.Events;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

final public class BrowserUtil {
    public static void browse(@NotNull URL url) {
        Tracking.appTracking().trace(Events.linkClick(null, url));
        com.intellij.ide.BrowserUtil.browse(url);
    }
}
