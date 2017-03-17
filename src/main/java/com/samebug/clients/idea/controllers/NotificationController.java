package com.samebug.clients.idea.controllers;

import com.intellij.ide.DataManager;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationsConfiguration;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.samebug.clients.common.api.entities.helpRequest.HelpRequest;
import com.samebug.clients.idea.messages.IncomingHelpRequest;

public final class NotificationController {
    public static final String PROFILE = "Samebug profile updates";

    public NotificationController() {
        NotificationsConfiguration.getNotificationsConfiguration().register(PROFILE, NotificationDisplayType.BALLOON, false);
    }

    public void incomingHelpRequest(final HelpRequest helpRequest) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
                for (Project p : openProjects) {
                    p.getMessageBus().syncPublisher(IncomingHelpRequest.TOPIC).addHelpRequest(helpRequest);
                }
                if (openProjects.length != 0) {
                    Project projectToShowPopup = null;

                    // get project from focus
                    DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
                    projectToShowPopup = DataKeys.PROJECT.getData(dataContext);

                    // get last opened project that must not be null
                    if (projectToShowPopup == null) {
                        projectToShowPopup = openProjects[openProjects.length - 1];
                    }

                    projectToShowPopup.getMessageBus().syncPublisher(IncomingHelpRequest.TOPIC).showHelpRequest(helpRequest);
                }
            }
        });
    }

}

