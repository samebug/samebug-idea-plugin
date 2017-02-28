package com.samebug.clients.idea.ui;

import com.samebug.clients.swing.ui.ColorUtil;

final class TestColorUtil extends ColorUtil {
    final boolean isUnderDarcula = false;

    @Override
    protected <T> T _forCurrentTheme(T[] objects) {
        if (objects == null) return null;
        else if (isUnderDarcula && objects.length > 1) return objects[1];
        else return objects[0];
    }
}
