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
package com.samebug.clients.idea.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.resources.SamebugIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by poroszd on 2/15/16.
 */
public class OperationalNotification extends Notification {
    public OperationalNotification(final Project project, String title, String content) {
        super(SamebugNotifications.SAMEBUG_OPERATIONAL_NOTIFICATIONS,
                title,
                content,
                NotificationType.INFORMATION,
                SamebugNotifications.basicNotificationListener(project, "help"));
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SamebugIcons.notification;
    }
}
