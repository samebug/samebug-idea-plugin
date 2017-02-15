package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.component.util.SamebugButton;
import com.samebug.clients.idea.ui.component.util.SamebugMultiLineLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class WriteTipCTA extends JPanel {
    private final Model model;
    private final MessageBus messageBus;

    public WriteTipCTA(MessageBus messageBus, Model model) {
        this.messageBus = messageBus;
        this.model = new Model(model);

        final SamebugButton button = new SamebugButton();
        button.setText(SamebugBundle.message("samebug.component.tip.write.cta.button"));
        final SamebugMultiLineLabel label = new SamebugMultiLineLabel();
        label.setText(SamebugBundle.message("samebug.component.tip.write.cta.label", model.usersWaitingHelp));

        setOpaque(false);
        setLayout(new MigLayout("fillx, w 300", "20[fill]50[fill]10", "20[fill]20"));
        add(button, "cell 0 0");
        add(label, "cell 1 0, wmin 0");
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = DrawUtil.init(g);

        g2.setColor(ColorUtil.separator());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
    }

    public final static class Model {
        private final int usersWaitingHelp;

        public Model(Model rhs) {
            this(rhs.usersWaitingHelp);
        }

        public Model(int usersWaitingHelp) {
            this.usersWaitingHelp = usersWaitingHelp;
        }
    }
}
