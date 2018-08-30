/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.idea.controllers;

import com.intellij.ide.DataManager;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationsConfiguration;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.http.entities.notification.Notification;
import com.samebug.clients.http.websocket.NotificationHandler;

public final class NotificationController implements NotificationHandler {
    static final Logger LOGGER = Logger.getInstance(NotificationController.class);
    public static final String PROFILE = "Samebug profile updates";

    public NotificationController() {
        NotificationsConfiguration.getNotificationsConfiguration().register(PROFILE, NotificationDisplayType.BALLOON, false);
    }

    @Override
    public void otherNotificationType(Notification notification) {
        LOGGER.warn("Unhandled incoming notification: " + notification);
    }

    private Project selectProjectToShowPopup(Project[] openProjects) {
        assert openProjects.length > 0;
        Project projectToShowPopup;

        // get project from focus
        DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
        projectToShowPopup = dataContext == null ? null : DataKeys.PROJECT.getData(dataContext);

        // get last opened project that must not be null
        if (projectToShowPopup == null) {
            projectToShowPopup = openProjects[openProjects.length - 1];
        }

        return projectToShowPopup;
    }
}
