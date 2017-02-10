package com.samebug.clients.idea.ui.component.util.tabbedPane;

import net.miginfocom.swing.MigLayout;

public final class FirstTabHeader extends SamebugTabHeader {
    public FirstTabHeader(String tabName, int hits) {
        super(tabName, hits);

        setLayout(new MigLayout("", "0[]7[]20", "0[20, fill]0"));
        setSelected(true);
    }
}
