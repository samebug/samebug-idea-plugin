/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.components.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.Colors;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.components.application.TutorialApplicationComponent;
import com.samebug.clients.idea.components.application.TutorialSettings;
import com.samebug.clients.idea.messages.model.ConnectionStatusListener;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.search.api.client.ConnectionStatus;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * I found no reasonable place for the one-time welcome message.
 * IdeaSamebugPlugin would be a better place, but I wanted to make sure there is an opened
 * project in scope, so clicking on the notification can open the samebug toolbar.
 */
public class TutorialProjectComponent extends AbstractProjectComponent implements ConnectionStatusListener {
    private final static Logger LOGGER = Logger.getInstance(TutorialProjectComponent.class);
    private boolean apiStatusNotificationShowed;

    protected TutorialProjectComponent(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        super.projectOpened();
        final TutorialSettings pluginState = ApplicationManager.getApplication().getComponent(TutorialApplicationComponent.class).getState();
        if (pluginState != null && pluginState.firstRun) {
            pluginState.firstRun = false;
            // At this point, the Samebug toolwindow is likely not initialized, so we delay the notification
            final int DELAY_MS = 15 * 1000;
            final Timer timer = new Timer(DELAY_MS, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        public void run() {
                            try {
                                // TODO cannot use the same tutorial notification as elsewhere, because I can't find how to access the toolbar button component
                                ToolWindowManager.getInstance(myProject).notifyByBalloon(
                                        "Samebug",
                                        MessageType.INFO, SamebugBundle.message("samebug.tutorial.welcome.message"),
                                        SamebugIcons.info,
                                        SamebugNotifications.basicHyperlinkListener(myProject, "tutorial"));
                                Tracking.projectTracking(myProject).trace(Events.pluginInstall());
                            } catch (IllegalStateException e1) {
                                LOGGER.warn("Samebug tool window was not initialized after "
                                        + DELAY_MS + " millis, welcome message could not be displayed", e1);
                            } catch (Exception e2) {
                                LOGGER.warn("Welcome message could not be displayed", e2);
                            }
                        }
                    });
                }
            });
            timer.setRepeats(false);
            timer.start();
        }


        MessageBusConnection projectConnection = myProject.getMessageBus().connect(myProject);
        projectConnection.subscribe(ConnectionStatusListener.TOPIC, this);
        Tracking.projectTracking(myProject).trace(Events.projectOpen(myProject));
    }

    @Override
    public void projectClosed() {
        Tracking.projectTracking(myProject).trace(Events.projectClose(myProject));
    }

    public static Balloon createTutorialBalloon(final Project project, final JComponent content) {
        final JPanel controlPanel = new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                add(new TransparentPanel() {
                    {
                        setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 16));
                        add(new JLabel(SamebugIcons.info));
                    }
                }, BorderLayout.WEST);
                add(content, BorderLayout.CENTER);
                setPreferredSize(new Dimension(450, getPreferredSize().height));
            }
        };
        return JBPopupFactory.getInstance().createBalloonBuilder(controlPanel)
                .setFillColor(Colors.samebugOrange)
                .setBorderColor(Colors.samebugOrange)
                .setDisposable(project)
                .createBalloon();

    }

    public static <T> T withTutorialProject(final Project project, TutorialProjectAnonfun<T> anonfun) {
        TutorialApplicationComponent tutorialApplicationComponent = ApplicationManager.getApplication().getComponent(TutorialApplicationComponent.class);
        TutorialProjectComponent component = project.getComponent(TutorialProjectComponent.class);
        if (tutorialApplicationComponent != null && tutorialApplicationComponent.getState() != null) {
            TutorialSettings settings = tutorialApplicationComponent.getState();
            anonfun.component = component;
            anonfun.settings = settings;
            anonfun.project = project;
            return anonfun.call();
        } else {
            return null;
        }
    }

    @Override
    public void startRequest() {
    }

    @Override
    public void connectionChange(boolean isConnected) {
    }

    @Override
    public void authenticationChange(boolean isAuthenticated) {
    }

    @Override
    public void apiStatusChange(@Nullable String apiStatus) {
        if (!apiStatusNotificationShowed && apiStatus != null) {
            apiStatusNotificationShowed = true;
            if (apiStatus.startsWith("DEPRECATED")) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ToolWindowManager.getInstance(myProject).notifyByBalloon(
                                "Samebug",
                                MessageType.WARNING, SamebugBundle.message("samebug.tutorial.apiStatus.deprecated"),
                                SamebugIcons.info, null);
                    }
                });
            } else if (apiStatus.startsWith("CLOSED")) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ToolWindowManager.getInstance(myProject).notifyByBalloon(
                                "Samebug",
                                MessageType.WARNING, SamebugBundle.message("samebug.tutorial.apiStatus.closed"),
                                SamebugIcons.info, null);
                    }
                });
            }
        }
    }

    @Override
    public void finishRequest(ConnectionStatus status) {
    }




    public static abstract class TutorialProjectAnonfun<T> {
        protected TutorialProjectComponent component;
        protected TutorialSettings settings;
        protected Project project;

        public abstract T call();
    }
}

