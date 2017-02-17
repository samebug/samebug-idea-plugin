package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.button.SamebugButton;
import com.samebug.clients.idea.ui.component.util.label.SamebugLabel;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class MarkButton extends SamebugButton {
    private Model model;
    private final MessageBus messageBus;

    private final CounterLabel counter;
    private final Separator separator;
    private final MarkLabel markLabel;

    public MarkButton(MessageBus messageBus, Model model) {
        super();
        this.model = new Model(model);
        this.messageBus = messageBus;

        counter = new CounterLabel();
        separator = new Separator();
        markLabel = new MarkLabel();

        setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        setLayout(new MigLayout("", "12[]9[]10[]8", "8[]8"));

        add(counter, ", h 16!");
        add(separator, "w 1!, h 16!");
        add(markLabel, ", h 16!");

        setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 14));
        setForeground(ColorUtil.MarkInteraction);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getListener().markClicked(MarkButton.this, getSolutionId(), MarkButton.this.model.userMarkId);
            }
        });

        update(model);
        updateUI();
    }

    public void setLoading() {
        MarkButton.this.setEnabled(false);
    }

    public void update(Model model) {
        MarkButton.this.setEnabled(true);
        this.model = new Model(model);

        if (model.userMarkId == null) setFilled(false);
        else setFilled(true);

        // Cheat. If the button is filled, we use the background color of the button as foreground color of the child components
        Color childrenForeground;
        if (MarkButton.this.isFilled()) childrenForeground = getBackground();
        else childrenForeground = getForeground();
        for (Component c : getComponents()) c.setForeground(childrenForeground);

        counter.update();
        markLabel.update();
    }

    public void setError() {
        update(model);
    }

    @Override
    protected void paintContent(Graphics2D g2) {
        super.paintChildren(g2);
    }

    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        // when the button is not filled, we have to change the foreground of the children
        if (!isFilled()) {
            for (Component c : getComponents()) c.setForeground(color);
        }
    }

    private final class CounterLabel extends SamebugLabel {
        {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(MarkButton.this.getFont());
        }

        void update() {
            setText(Integer.toString(model.marks));
        }
    }

    private final class MarkLabel extends SamebugLabel {
        {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(MarkButton.this.getFont());
        }

        void update() {
            if (MarkButton.this.model.userMarkId == null) setText(SamebugBundle.message("samebug.component.mark.mark"));
            else setText(SamebugBundle.message("samebug.component.mark.marked"));
        }
    }

    private final class Separator extends JComponent {
        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = DrawUtil.init(g);
            g2.setColor(getForeground());

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

    private Integer getSolutionId() { return DataManager.getInstance().getDataContext(this).getData(SolutionId); }
    private Listener getListener() {
        return messageBus.syncPublisher(Listener.TOPIC);
    }

    public interface Listener {
        Topic<Listener> TOPIC = Topic.create("MarkPanel", Listener.class);

        void markClicked(MarkButton markButton, Integer solutionId, Integer currentMarkId);
    }

    public static final DataKey<Integer> SolutionId = DataKey.create("SolutionId");
}
