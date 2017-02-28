package com.samebug.clients.idea.components.application;

import com.intellij.openapi.util.IconLoader;
import com.samebug.clients.swing.ui.SamebugIcons;

import javax.swing.*;

final class IdeaSamebugIcons extends SamebugIcons {
    @Override
    protected Icon getImage(String path) {
        return IconLoader.getIcon(path);
    }
}
