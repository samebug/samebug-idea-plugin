package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.ISolutionFrame;
import com.samebug.clients.idea.ui.component.profile.ProfilePanel;
import com.samebug.clients.idea.ui.component.util.button.SamebugButton;
import com.samebug.clients.idea.ui.component.util.label.SamebugLabel;
import com.samebug.clients.idea.ui.component.util.multiline.CenteredMultilineLabel;
import com.samebug.clients.idea.ui.component.util.panel.EmphasizedPanel;
import com.samebug.clients.idea.ui.component.util.panel.SamebugPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class SolutionFrame extends SamebugPanel implements ISolutionFrame {
    private Solutions solutions;
    private final MessageBus messageBus;

    public SolutionFrame(MessageBus messageBus) {
        this.messageBus = messageBus;

        setLayout(new BorderLayout());
        setLoading();
    }

    public void setGenericError() {
        removeAll();
        add(new ReloadableErrorPanel("some generic error happened"));
        revalidate();
        repaint();
    }

    @Override
    public void setRetriableError() {
        removeAll();
        add(new ReloadableErrorPanel("probably timeout"));
        revalidate();
        repaint();
    }

    @Override
    public void setServerError() {
        removeAll();
        add(new ReloadableErrorPanel("internal server error"));
        revalidate();
        repaint();
    }

    @Override
    public void setAuthenticationError() {
        removeAll();
        add(new ReloadableErrorPanel("bad api key"));
        revalidate();
        repaint();
    }

    @Override
    public void setAuthorizationError() {
        removeAll();
        add(new ReloadableErrorPanel("access denied"));
        revalidate();
        repaint();
    }

    @Override
    public void setLoading() {
        removeAll();
        add(new SamebugLabel("loading"));
        revalidate();
        repaint();
    }

    public void setContent(Model model) {
        solutions = new Solutions(model);
        removeAll();
        add(solutions.exceptionHeader, BorderLayout.NORTH);
        add(solutions.tabs, BorderLayout.CENTER);
        add(solutions.profilePanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private final class Solutions {
        private final Model model;
        private final JPanel exceptionHeader;
        private final ResultTabs tabs;
        private final JPanel profilePanel;

        Solutions(Model model) {
            exceptionHeader = new ExceptionHeaderPanel(messageBus, model.header);
            tabs = new ResultTabs(messageBus, model.resultTabs);
            profilePanel = new ProfilePanel(messageBus, model.profilePanel);
            this.model = model;
        }
    }

    private final class ReloadableErrorPanel extends EmphasizedPanel {
        protected final CenteredMultilineLabel label;

        public ReloadableErrorPanel(String text) {
            final SamebugButton button = new SamebugButton("Reload", true);
            label = new CenteredMultilineLabel();
            label.setText(text);

            setLayout(new MigLayout("fillx, w 300", "40[]40", "40[]20[]40"));
            add(label, "cell 0 0, wmin 0, growx");
            add(button, "cell 0 1, align center");
        }

    }
}
