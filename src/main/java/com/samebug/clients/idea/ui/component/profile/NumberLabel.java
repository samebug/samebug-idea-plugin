package com.samebug.clients.idea.ui.component.profile;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.label.SamebugLabel;
import com.samebug.clients.idea.ui.component.util.panel.TransparentPanel;
import net.miginfocom.swing.MigLayout;

import java.awt.*;

public class NumberLabel extends TransparentPanel {
    protected final SamebugLabel numberLabel;
    protected final SamebugLabel hintLabel;

    public NumberLabel(int number, String hint) {
        numberLabel = new SamebugLabel(Integer.toString(number), FontRegistry.AvenirDemi, 14);
        hintLabel = new SamebugLabel(hint, FontRegistry.AvenirDemi, 12);

        setLayout(new MigLayout("fillx", "0[]4[]0", "0[]0"));

        add(numberLabel, "");
        add(hintLabel, "");

        updateUI();
    }

    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        for (Component c : getComponents()) c.setForeground(color);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setForeground(ColorUtil.forCurrentTheme(ColorUtil.Text));
    }
}
