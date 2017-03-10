/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.frame.solution;

import com.samebug.clients.common.ui.frame.solution.ISolutionFrame;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.errorBarPane.BasicFrame;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.CenteredMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.component.profile.ProfilePanel;
import com.samebug.clients.swing.ui.modules.IconService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class SolutionFrame extends BasicFrame implements ISolutionFrame {
    private Solutions solutions;

    public SolutionFrame() {
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
            exceptionHeader = new SearchHeaderPanel(model.header);
            tabs = new ResultTabs(model.resultTabs);
            profilePanel = new ProfilePanel(model.profilePanel);

            setLayout(new BorderLayout());
            add(exceptionHeader, BorderLayout.NORTH);
            add(tabs, BorderLayout.CENTER);
            add(profilePanel, BorderLayout.SOUTH);
        }
    }

    private class ErrorPanel extends TransparentPanel {
        public ErrorPanel(String description, String buttonLabel, MouseListener mouseListener) {
            final JLabel alertImage = new JLabel(IconService.alert());
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
            super(MessageService.message("samebug.component.error.connection.description"),
                    MessageService.message("samebug.component.error.connection.button"),
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
            super(MessageService.message("samebug.component.error.network.description"),
                    MessageService.message("samebug.component.error.network.button"),
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
            super(MessageService.message("samebug.component.error.authentication.description"),
                    MessageService.message("samebug.component.error.authentication.button"),
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
            super(MessageService.message("samebug.component.error.general.description"),
                    MessageService.message("samebug.component.error.general.button"),
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            getListener().reload();
                        }
                    });
        }
    }

    private Listener getListener() {
        return ListenerService.getListener(this, ISolutionFrame.Listener.class);
    }
}
