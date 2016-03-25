/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.wm.ToolWindowManager;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Settings;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.notification.TutorialNotification;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.tracking.Events;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by poroszd on 2/15/16.
 * <p/>
 * I found no reasonable place for the one-time welcome message.
 * IdeaSamebugPlugin would be a better place, but I wanted to make sure there is an opened
 * project in scope, so clicking on the notification can open the samebug toolbar.
 * <p/>
 * Renaming this class or moving this functionality is welcomed.
 */
public class HelpComponent extends AbstractProjectComponent {
    protected HelpComponent(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        super.projectOpened();
        final Settings pluginState = IdeaSamebugPlugin.getInstance().getState();
        if (pluginState != null && pluginState.isFirstRun()) {
            // At this point, the Samebug toolwindow is likely not initialized, so we delay the notification
            final int DELAY_MS = 15 * 1000;
            final Timer timer = new Timer(DELAY_MS, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        public void run() {
                            try {
                                ToolWindowManager.getInstance(myProject).notifyByBalloon(
                                        "Samebug",
                                        MessageType.INFO, SamebugBundle.message("samebug.notification.help.welcome.message"),
                                        SamebugIcons.notification,
                                        SamebugNotifications.basicHyperlinkListener(myProject, "help"));
                                Tracking.projectTracking(myProject).trace(Events.pluginInstall());
                                pluginState.setFirstRun(false);
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


        Tracking.projectTracking(myProject).trace(Events.projectOpen(myProject));
    }

    @Override
    public void projectClosed() {
        Tracking.projectTracking(myProject).trace(Events.projectClose(myProject));
    }

    public boolean offerSearchNotification(String searchNotification, SearchNotificationTutorialCase tutorialCase) {
        if (tutorialCase == null) return false;

        final Settings pluginState = IdeaSamebugPlugin.getInstance().getState();
        String htmlMessage = null;

        // TODO notification tutorial states
        switch (tutorialCase) {
            case RECURRING_EXCEPTIONS:
                htmlMessage = SamebugBundle.message("samebug.notification.tutorial.searchResults.recurring", searchNotification, SamebugIcons.calendarUrl);
                break;
            case ZERO_SOLUTION_EXCEPTIONS:
                htmlMessage = SamebugBundle.message("samebug.notification.tutorial.searchResults.zeroSolution", searchNotification, SamebugIcons.lightbulbUrl);
                break;
            case MIXED_EXCEPTIONS:
                htmlMessage = SamebugBundle.message("samebug.notification.tutorial.searchResults.mixed", searchNotification, SamebugIcons.calendarUrl, SamebugIcons.lightbulbUrl);
                break;
        }

        if (htmlMessage != null) {
            final TutorialNotification notification = new TutorialNotification(myProject, SamebugBundle.message("samebug.notification.searchresults.title"), htmlMessage);
            notification.notify(myProject);
            return true;
        } else {
            return false;
        }
    }

    private final static Logger LOGGER = Logger.getInstance(HelpComponent.class);

    public enum SearchNotificationTutorialCase {
        RECURRING_EXCEPTIONS,
        ZERO_SOLUTION_EXCEPTIONS,
        MIXED_EXCEPTIONS
    }
}
