package com.samebug.clients.idea.ui.component.util.tabbedPane;

import net.miginfocom.swing.MigLayout;

public final class NonFirstTabHeader extends SamebugTabHeader {
    public NonFirstTabHeader(String tabName, int hits) {
        super(tabName, hits);

        setLayout(new MigLayout("", "20[]7[]20", "0[20, fill]0"));
    }
}
