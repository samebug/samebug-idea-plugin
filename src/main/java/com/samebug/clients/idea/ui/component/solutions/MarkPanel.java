package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.intellij.util.ui.Animator;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;
import com.samebug.clients.idea.ui.component.util.label.SamebugLabel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class MarkPanel extends JPanel {
    private Model model;
    private final MessageBus messageBus;

    private final CounterLabel counter;
    private final MarkButton button;

    private final LoadingAnimation loadingAnimator;

    public MarkPanel(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        counter = new CounterLabel();
        final Separator separator = new Separator();
        button = new MarkButton();

        loadingAnimator = new LoadingAnimation();

        setBorder(null);
        setOpaque(false);
        setLayout(new MigLayout("", "12[]9[]10[]8", "8[]8"));


        add(counter, ", h 16!");
        add(separator, "w 1!, h 16!");
        add(button, ", h 16!");

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getListener().markClicked(MarkPanel.this, getSolutionId(), MarkPanel.this.model.userMarkId);
            }
        });

        update(model);
    }

    public void setLoading() {
        MarkPanel.this.setEnabled(false);
        loadingAnimator.reset();
        loadingAnimator.resume();
    }

    public void update(Model model) {
        MarkPanel.this.setEnabled(true);
        this.model = new Model(model);
        loadingAnimator.suspend();

        counter.update();
        button.update();
    }

    public void setError() {
        update(model);
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
        if (loadingAnimator.isRunning()) {
            loadingAnimator.paint(g2);
        } else {
            super.paint(g);
        }
    }


    // TODO handling pushed state, the whole panel should be the button
    private final class CounterLabel extends SamebugLabel {
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

        @Override
        public void updateUI() {
            super.updateUI();
            update();
        }
    }

    // TODO this could be a simple label instead of button
    private final class MarkButton extends JButton {
        {
            setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
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

        @Override
        public void updateUI() {
            setUI(new BasicButtonUI());
            update();
        }
    }

    private final class Separator extends JComponent {
        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = DrawUtil.init(g);
            if (model.userMarkId == null) g2.setColor(ColorUtil.mark());
            else g2.setColor(ColorUtil.background());

            g2.drawLine(0, 0, 0, 16);
        }
    }

    private final class LoadingAnimation extends Animator {
        private int currentFrame;

        public LoadingAnimation() {
            super("MarkPanelLoading", 100, 5000, true);
        }

        @Override
        public void paintNow(int frame, int totalFrames, int cycle) {
            currentFrame = frame;
            repaint();
        }

        public void paint(Graphics2D g2) {
            g2.setColor(Color.green);
            g2.fillRect(10,10,currentFrame * 2, getHeight() - 10);
        }
    };

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

        void markClicked(MarkPanel markPanel, Integer solutionId, Integer currentMarkId);
    }

    public static final DataKey<Integer> SolutionId = DataKey.create("SolutionId");
}
