package com.samebug.clients.swing.ui.component.authentication;

import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class Delimeter extends JComponent {
    private final String label = MessageService.message("samebug.component.authentication.delimeter");
    private final Color[] myColor = ColorService.FormSeparator;
    private final int padding = 6;
    {
        final Font f = FontService.regular(12);
        final SamebugLabel l = new SamebugLabel(label);
        l.setForegroundColor(myColor);

        setFont(f);
        l.setFont(f);

        setLayout(new MigLayout("fillx", "0[]0", "0[]0"));
        add(l, "align center");
    }
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = DrawService.init(g);
        g2.setColor(ColorService.forCurrentTheme(myColor));

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(label);
        int yMiddle = (getHeight() - 1) / 2;
        int componentWidth = getWidth();
        int xLength = (componentWidth - textWidth) / 2 - padding;
        g2.drawLine(0, yMiddle, xLength, yMiddle);
        g2.drawLine(componentWidth - xLength, yMiddle, componentWidth, yMiddle);
    }
}
