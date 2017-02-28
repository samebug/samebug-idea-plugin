package com.samebug.clients.idea.ui;

import com.samebug.clients.swing.ui.SamebugIcons;

import javax.swing.*;
import java.net.URL;

public final class TestSamebugIcons extends SamebugIcons {
    @Override
    protected Icon getImage(String path) {
        URL url = getClass().getResource(path);
        return new ImageIcon(url);
    }
}
