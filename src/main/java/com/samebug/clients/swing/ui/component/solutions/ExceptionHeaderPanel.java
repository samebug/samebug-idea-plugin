package com.samebug.clients.swing.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.IExceptionHeaderPanel;
import com.samebug.clients.swing.ui.ColorUtil;
import com.samebug.clients.swing.ui.FontRegistry;
import com.samebug.clients.swing.ui.component.util.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.component.util.panel.SamebugPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ExceptionHeaderPanel extends SamebugPanel implements IExceptionHeaderPanel {
    private final Model model;
    private final MessageBus messageBus;

    public ExceptionHeaderPanel(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        final HeaderTextLabel title = new HeaderTextLabel();
        setLayout(new MigLayout("fillx, filly", "0[]0", "30[]30"));

        add(title, "wmin 0, hmax 56");
    }

    final class HeaderTextLabel extends SamebugMultilineLabel {
        {
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            setFont(FontRegistry.demi(24));
            setForeground(ColorUtil.EmphasizedText);

            setText(model.title);

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().titleClicked();
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            // TODO this breaks when changing font
            if (getLineCount() <= 1) {
                return new Dimension(Integer.MAX_VALUE, 24 + 2);
            } else {
                return new Dimension(Integer.MAX_VALUE, 24 * 2 + 8);
            }
        }
    }

    private Listener getListener() {
        return messageBus.syncPublisher(Listener.TOPIC);
    }
}

