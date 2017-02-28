package com.samebug.clients.idea.components.application;

import com.intellij.util.ui.UIUtil;
import com.samebug.clients.swing.ui.ColorUtil;

final class IdeaColorUtil extends ColorUtil {
    @Override
    protected <T> T _forCurrentTheme(T[] objects) {
        if (objects == null) return null;
        else if (UIUtil.isUnderDarcula() && objects.length > 1) return objects[1];
        else return objects[0];
    }

}
