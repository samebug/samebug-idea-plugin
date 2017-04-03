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
package com.samebug.clients.swing.ui.base.frame;

import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.animation.LoadingAnimation;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.multiline.CenteredMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.IconService;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class BasicFrame extends ErrorBarPane implements IFrame {
    protected final ErrorBar networkErrorBar;
    protected final ErrorBar authenticationErrorBar;

    public BasicFrame() {
        networkErrorBar = new ErrorBar(MessageService.message("samebug.component.errorBar.network"));
        authenticationErrorBar = new ErrorBar(MessageService.message("samebug.component.errorBar.authentication"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // swallow click, so the toolwindow will not close when it is not in docked mode
                super.mouseClicked(e);
            }
        });
    }

    public void showNetworkError() {
        addErrorBar(networkErrorBar);
    }

    public void hideNetworkError() {
        removeErrorBar(networkErrorBar);
    }

    public void showAuthenticationError() {
        addErrorBar(authenticationErrorBar);
    }

    public void hideAuthenticationError() {
        removeErrorBar(authenticationErrorBar);
    }

    public void popupError(String message) {
        popupErrorBar(new ErrorBar(message));
    }

    @Override
    public void setLoading() {
        addMainComponent(new LoadingPanel());
    }

    @Override
    public void loadingFailedWithAuthenticationError() {
        addMainComponent(new AuthenticationErrorPanel());
    }

    @Override
    public void loadingFailedWithAuthorizationError() {
        addMainComponent(new GeneralErrorPanel());
    }

    @Override
    public void loadingFailedWithRetriableError() {
        addMainComponent(new ConnectionErrorPanel());
    }

    @Override
    public void loadingFailedWithNetworkError() {
        addMainComponent(new NetworkErrorPanel());
    }

    @Override
    public void loadingFailedWithServerError() {
        addMainComponent(new GeneralErrorPanel());
    }

    @Override
    public void loadingFailedWithGenericError() {
        addMainComponent(new GeneralErrorPanel());
    }

    protected abstract FrameListener getListener();

    private final class LoadingPanel extends TransparentPanel {
        public LoadingPanel() {
            final LoadingAnimation animation = new LoadingAnimation(40);
            final CenteredMultilineLabel label = new CenteredMultilineLabel();
            label.setText(MessageService.message("samebug.frame.loading"));
            setLayout(new MigLayout("fillx", "0:push[fill]0:push", "0:push[]15[]0:push"));
            add(animation, "cell 0 0, wmin 0, al center");
            add(label, "cell 0 1, wmin 0");
        }
    }

    private class ErrorPanel extends TransparentPanel {
        public ErrorPanel(String description, String buttonLabel, MouseListener mouseListener) {
            final JLabel alertImage = new JLabel(IconService.alert());
            final CenteredMultilineLabel label = new CenteredMultilineLabel();
            label.setText(description);
            final SamebugButton reloadButton = new SamebugButton(MessageService.message("samebug.component.error.general.button"), false);
            final MouseListener reloadAction = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().reload();
                    TrackingService.trace(Events.recoveryReload());
                }
            };
            reloadButton.addMouseListener(reloadAction);

            // TODO this is a bit hackish...
            if (buttonLabel != null && mouseListener != null) {
                final SamebugButton alternativeButton = new SamebugButton(buttonLabel, false);
                alternativeButton.addMouseListener(mouseListener);
                setLayout(new MigLayout("fillx", "0[]0", "0:push[]30[]30[]30[]0:push"));
                add(alternativeButton, "cell 0 2, al center");
                add(reloadButton, "cell 0 3, al center");
            } else {
                setLayout(new MigLayout("fillx", "0[]0", "0:push[]30[]30[]0:push"));
                add(reloadButton, "cell 0 2, al center");
            }
            add(alertImage, "cell 0 0, wmin 0, growx");
            add(label, "cell 0 1, wmin 0, growx");
        }
    }

    private final class ConnectionErrorPanel extends ErrorPanel {
        public ConnectionErrorPanel() {
            super(MessageService.message("samebug.component.error.connection.description"), null, null);
        }
    }

    private final class GeneralErrorPanel extends ErrorPanel {
        public GeneralErrorPanel() {
            super(MessageService.message("samebug.component.error.general.description"), null, null);
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
                            TrackingService.trace(Events.recoveryOpenIdeaSettings());
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
                            TrackingService.trace(Events.recoveryOpenSamebugSettings());
                        }
                    });
        }
    }

}
