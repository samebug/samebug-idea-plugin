package com.samebug.clients.idea.ui.controller.component;

import com.samebug.clients.common.ui.component.bugmate.IBugmateHit;
import com.samebug.clients.idea.ui.modules.BrowserUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public final class BugmateHitListener implements IBugmateHit.Listener {
    @Override
    public void profileClick(@NotNull IBugmateHit source, @Nullable URL profileUrl) {
        if (profileUrl != null) BrowserUtil.browse(profileUrl);
    }
}
