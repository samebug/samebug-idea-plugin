package com.samebug.clients.idea.components.project;

import com.intellij.notification.Notification;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Settings;
import com.samebug.clients.idea.notification.OperationalNotification;
import com.samebug.clients.idea.resources.SamebugBundle;

/**
 * Created by poroszd on 2/15/16.
 *
 * I found no reasonable place for the one-time welcome message.
 * IdeaSamebugPlugin would be a better place, but I wanted to make sure there is an opened
 * project in scope, so clicking on the notification can open the samebug toolbar.
 *
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
        if (pluginState.isFirstRun()) {
            Notification n = new OperationalNotification(myProject,
                    SamebugBundle.message("samebug.notification.operational.welcome.title"),
                    SamebugBundle.message("samebug.notification.operational.welcome.message"));
            n.notify(myProject);
            pluginState.setFirstRun(false);
        }

    }

    @Override
    public void projectClosed() {
    }

}
