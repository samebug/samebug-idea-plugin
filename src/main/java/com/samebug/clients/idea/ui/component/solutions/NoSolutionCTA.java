package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public final class NoSolutionCTA extends HelpOthersCTA {
    private final HelpOthersCTA.Model model;
    private final MessageBus messageBus;

    private final CenteredMultiLineLabel label;

    public NoSolutionCTA(MessageBus messageBus, HelpOthersCTA.Model model) {
        this.messageBus = messageBus;
        this.model = new HelpOthersCTA.Model(model);

        final FilledButton button = new FilledButton();
        button.setText(SamebugBundle.message("samebug.component.tip.write.cta.button"));
        label = new CenteredMultiLineLabel();

        setLayout(new MigLayout("fillx, w 300", "40[]40", "40[]20[]40"));
        add(label, "cell 0 0, wmin 0, growx");
        add(button, "cell 0 1, align center");
    }

    // TODO this is not a good way to reuse this component
    public void setTextForSolutions() {
        label.setText(SamebugBundle.message("samebug.component.cta.writeTip.noWebHits.label", model.usersWaitingHelp));
    }

    // TODO this is not a good way to reuse this component
    public void setTextForTips() {
        label.setText(SamebugBundle.message("samebug.component.cta.writeTip.noTipHits.label", model.usersWaitingHelp));
    }

    private final class CenteredMultiLineLabel extends JTextPane {
        {
            setEditable(false);
            setCursor(null);
            setFocusable(false);
            setFont(new Font(FontRegistry.AvenirRegular, Font.PLAIN, 16));
            StyledDocument doc = getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);
            setOpaque(false);
        }

        @Override
        public void updateUI() {
            super.updateUI();
            setBackground(ColorUtil.text());
        }
    }

    // TODO merge with SamebugButton
    // TODO find out if it is simpler to use a custom ButtonUI
    private final class FilledButton extends JButton {
        {
            setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 12));
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 14));
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = DrawUtil.init(g);

            // draw the rounded border
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
            // the button content is drawed by the default implementation
            super.paint(g);
        }

        @Override
        public void updateUI() {
            setUI(new BasicButtonUI());
            // TODO mimic transparent label
            setForeground(ColorUtil.background());
            setBackground(ColorUtil.samebug());
        }
    }

}
