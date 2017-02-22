package com.samebug.clients.idea.ui.component.util.button;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class SamebugButtonUI extends BasicButtonUI {
    // Override because the color of the text depends on the fill style of the samebug button
    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        assert c instanceof SamebugButton : "SamebugButtonUI is only applicable to SamebugButton";
        SamebugButton b = (SamebugButton) c;
        FontMetrics fm = b.getFontMetrics(b.getFont());

        if (b.isFilled()) g.setColor(b.getBackground());
        else g.setColor(b.getForeground());

        sun.swing.SwingUtilities2.drawString(c, g, text, textRect.x + getTextShiftOffset(), textRect.y + fm.getAscent() + getTextShiftOffset());
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
    }
}
