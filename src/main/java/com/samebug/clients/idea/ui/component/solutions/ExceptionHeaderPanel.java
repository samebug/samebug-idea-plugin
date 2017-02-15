package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.SamebugMultiLineLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ExceptionHeaderPanel extends JPanel {
    private final Model model;
    private final MessageBus messageBus;

    public ExceptionHeaderPanel(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        final HeaderTextLabel title = new HeaderTextLabel();
        title.setText(model.title);

        setLayout(new MigLayout("fillx, filly", "0[]0", "30[]30"));

        add(title, "wmin 0, hmax 56");
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(ColorUtil.background());
    }

    final class HeaderTextLabel extends SamebugMultiLineLabel {
        {
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 24));

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

    public static final class Model {
        private final String title;

        public Model(Model rhs) {
            this(rhs.title);
        }

        public Model(String title) {
            this.title = title;
        }
    }

    public interface Listener {
        Topic<Listener> TOPIC = Topic.create("ExceptionHeaderPanel", Listener.class);

        void titleClicked();
    }
}

