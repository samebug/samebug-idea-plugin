package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.SamebugMultiLineLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class ExceptionHeaderPanel extends JPanel {
    private final Model model;
    private final MessageBus messageBus;

    public ExceptionHeaderPanel(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        final HeaderTextLabel title = new HeaderTextLabel();
        title.setText(model.title);

        setBackground(ColorUtil.background());
        setLayout(new MigLayout("fillx, filly", "0[]0", "30[]30"));

        add(title, "wmin 0, hmax 56");
    }

    final class HeaderTextLabel extends SamebugMultiLineLabel {
        {
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 24));
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

    public static final class Model {
        private final String title;

        public Model(Model rhs) {
            this(rhs.title);
        }

        public Model(String title) {
            this.title = title;
        }
    }
}

