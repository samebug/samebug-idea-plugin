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

import com.intellij.notification.Notification;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Settings;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.notification.OperationalNotification;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.tracking.Events;

/**
 * Created by poroszd on 2/15/16.
 * <p/>
 * I found no reasonable place for the one-time welcome message.
 * IdeaSamebugPlugin would be a better place, but I wanted to make sure there is an opened
 * project in scope, so clicking on the notification can open the samebug toolbar.
 * <p/>
 * Renaming this class or moving this functionality is welcomed.
 */
public class UtilComponent extends AbstractProjectComponent {
    protected UtilComponent(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        super.projectOpened();
        Settings pluginState = IdeaSamebugPlugin.getInstance().getState();
        if (pluginState != null && pluginState.isFirstRun()) {
            Notification n = new OperationalNotification(myProject,
                    SamebugBundle.message("samebug.notification.operational.welcome.title"),
                    SamebugBundle.message("samebug.notification.operational.welcome.message"));
            n.notify(myProject);
            Tracking.projectTracking(myProject).trace(Events.pluginInstall());
            pluginState.setFirstRun(false);
        }


        Tracking.projectTracking(myProject).trace(Events.projectOpen(myProject));
    }

    @Override
    public void projectClosed() {
        Tracking.projectTracking(myProject).trace(Events.projectClose(myProject));
    }

}
