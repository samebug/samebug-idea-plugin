package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public final class MarkPanel extends JPanel {
    private Model model;
    private final MessageBus messageBus;

    private final CounterLabel counter;
    private final MarkButton button;

    public MarkPanel(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        counter = new CounterLabel();
        final Separator separator = new Separator();
        button = new MarkButton();

        setBorder(null);
        setOpaque(false);
        setLayout(new MigLayout("", "12[]9[]10[]8", "8[]8"));


        add(counter, ", h 16!");
        add(separator, "w 1!, h 16!");
        add(button, ", h 16!");

        update(model);
    }

    public void update(Model model) {
        this.model = new Model(model);

        counter.update();
        button.update();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = DrawUtil.init(g);
        g2.setColor(ColorUtil.mark());
        // TODO extract drawing rounded rect, it happens for every button and panel
        if (model.userMarkId == null) {
            g2.drawRoundRect(0, 0, getPreferredSize().width - 1, getPreferredSize().height - 1, 5, 5);
        } else {
            g2.fillRoundRect(0, 0, getPreferredSize().width - 1, getPreferredSize().height - 1, 5, 5);

        }
        super.paint(g);
    }


    private final class CounterLabel extends JLabel {
        {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 14));
            update();
        }

        void update() {
            if (model.userMarkId == null) setForeground(ColorUtil.mark());
            else setForeground(ColorUtil.background());

            setText(Integer.toString(model.marks));
        }
    }

    private final class MarkButton extends JButton {
        {
            setBorder(null);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 14));
            update();
        }

        void update() {
            if (MarkPanel.this.model.userMarkId == null) {
                setForeground(ColorUtil.mark());
                setText(SamebugBundle.message("samebug.component.mark.mark"));
            } else {
                setForeground(ColorUtil.background());
                setText(SamebugBundle.message("samebug.component.mark.marked"));
            }
        }
    }

    private final class Separator extends JComponent {
        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = DrawUtil.init(g);
            if (model.userMarkId == null) g2.setColor(ColorUtil.markSeparator());
            else g2.setColor(ColorUtil.markedSeparator());

            g2.drawLine(0, 0, 0, 16);
        }
    }

    public static final class Model {
        private final int marks;
        @Nullable
        private final Integer userMarkId;
        private final boolean userCanMark;

        public Model(Model rhs) {
            this(rhs.marks, rhs.userMarkId, rhs.userCanMark);
        }

        public Model(int marks, @Nullable Integer userMarkId, boolean userCanMark) {
            this.marks = marks;
            this.userMarkId = userMarkId;
            this.userCanMark = userCanMark;
        }
    }
}
