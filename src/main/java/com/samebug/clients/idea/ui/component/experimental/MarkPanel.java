package com.samebug.clients.idea.ui.component.experimental;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.resources.SamebugBundle;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class MarkPanel extends JPanel {
    Model model;

    @NotNull
    final CounterLabel counter;
    @NotNull
    final MarkButton button;
    @NotNull
    final MessageBus messageBus;

    public MarkPanel(MessageBus messageBus, Model model) {
        this.messageBus = messageBus;

        counter = new CounterLabel();
        button = new MarkButton();

        setBorder(null);
        setBackground(Color.white);
        setLayout(new MigLayout(
                "",
                "0[]1[]0",
                "0[]0"));
        add(counter, "w 40!, h 40!");
        add(button, "w 120!, h 40!");

        update(model);
    }

    public void update(Model model) {

        this.model = new Model(model);

        if (model.userMarkId == null) {
            if (model.userCanMark) {
                button.setText(SamebugBundle.message("samebug.mark.mark"));
            } else {
                // TODO
            }
        } else {
            button.setText(SamebugBundle.message("samebug.mark.marked"));

        }
        counter.setText(Integer.toString(model.marks));
    }


    final class CounterLabel extends JLabel {
        {
            setBorder(null);
            setForeground(Constants.TextColor);
            setBackground(Constants.MarkPanelBackgroundColor);
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 14));
        }
    }

    final class MarkButton extends JButton {
        {
            setBorder(null);
            setContentAreaFilled(false);
            setForeground(Constants.TextColor);
            setBackground(Constants.MarkPanelBackgroundColor);
            setOpaque(true);
            setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 14));
        }
    }

    public static final class Model {
        public int marks;
        public
        @Nullable
        Integer userMarkId;
        public boolean userCanMark;

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
