package com.samebug.clients.swing.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.ISolutionFrame;
import com.samebug.clients.swing.ui.SamebugBundle;
import com.samebug.clients.swing.ui.SamebugIcons;
import com.samebug.clients.swing.ui.component.profile.ProfilePanel;
import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.errorBarPane.BasicFrame;
import com.samebug.clients.swing.ui.component.util.errorBarPane.ErrorBar;
import com.samebug.clients.swing.ui.component.util.errorBarPane.ErrorBarPane;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import com.samebug.clients.swing.ui.component.util.multiline.CenteredMultilineLabel;
import com.samebug.clients.swing.ui.component.util.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class SolutionFrame extends BasicFrame implements ISolutionFrame {
    private Solutions solutions;
    private final MessageBus messageBus;

    public SolutionFrame(MessageBus messageBus) {
        this.messageBus = messageBus;

        setLoading();
    }

    public void loadingFailedWithAuthenticationError() {
        addMainComponent(new AuthenticationErrorPanel());
    }

    public void loadingFailedWithAuthorizationError() {
        addMainComponent(new GeneralErrorPanel());
    }

    public void loadingFailedWithRetriableError() {
        addMainComponent(new ConnectionErrorPanel());
    }

    public void loadingFailedWithNetworkError() {
        addMainComponent(new NetworkErrorPanel());
    }

    public void loadingFailedWithServerError() {
        addMainComponent(new GeneralErrorPanel());
    }

    public void loadingFailedWithGenericError() {
        addMainComponent(new GeneralErrorPanel());
    }

    public void setLoading() {
        addMainComponent(new SamebugLabel("loading"));
    }

    public void loadingSucceeded(Model model) {
        solutions = new Solutions(model);
        addMainComponent(solutions);
    }

    private final class Solutions extends SamebugPanel {
        private final JPanel exceptionHeader;
        private final ResultTabs tabs;
        private final JPanel profilePanel;

        Solutions(Model model) {
            exceptionHeader = new ExceptionHeaderPanel(messageBus, model.header);
            tabs = new ResultTabs(messageBus, model.resultTabs);
            profilePanel = new ProfilePanel(messageBus, model.profilePanel);

            setLayout(new BorderLayout());
            add(exceptionHeader, BorderLayout.NORTH);
            add(tabs, BorderLayout.CENTER);
            add(profilePanel, BorderLayout.SOUTH);
        }
    }

    private class ErrorPanel extends TransparentPanel {
        public ErrorPanel(String description, String buttonLabel, MouseListener mouseListener) {
            final JLabel alertImage = new JLabel(SamebugIcons.alert());
            final CenteredMultilineLabel label = new CenteredMultilineLabel();
            final SamebugButton button = new SamebugButton(buttonLabel, false);
            label.setText(description);
            button.addMouseListener(mouseListener);

            setLayout(new MigLayout("fillx, al center center", "0[]0", "0:push[]30[]30[]0:push"));
            add(alertImage, "cell 0 0, wmin 0, growx");
            add(label, "cell 0 1, wmin 0, growx");
            add(button, "cell 0 2, align center");
        }
    }

    private final class ConnectionErrorPanel extends ErrorPanel {
        public ConnectionErrorPanel() {
            super(SamebugBundle.message("samebug.component.error.connection.description"),
                    SamebugBundle.message("samebug.component.error.connection.button"),
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            getListener().reload();
                        }
                    });
        }
    }

    private final class NetworkErrorPanel extends ErrorPanel {
        public NetworkErrorPanel() {
            super(SamebugBundle.message("samebug.component.error.network.description"),
                    SamebugBundle.message("samebug.component.error.network.button"),
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            getListener().openNetworkSettings();
                        }
                    });
        }
    }

    private final class AuthenticationErrorPanel extends ErrorPanel {
        public AuthenticationErrorPanel() {
            super(SamebugBundle.message("samebug.component.error.authentication.description"),
                    SamebugBundle.message("samebug.component.error.authentication.button"),
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            getListener().openSamebugSettings();
                        }
                    });
        }
    }

    private final class GeneralErrorPanel extends ErrorPanel {
        public GeneralErrorPanel() {
            super(SamebugBundle.message("samebug.component.error.general.description"),
                    SamebugBundle.message("samebug.component.error.general.button"),
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            getListener().reload();
                        }
                    });
        }
    }

    private Listener getListener() {
        return messageBus.syncPublisher(Listener.TOPIC);
    }
}
