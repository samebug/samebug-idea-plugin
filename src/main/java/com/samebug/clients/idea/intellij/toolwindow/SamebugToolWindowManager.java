package com.samebug.clients.idea.intellij.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import com.samebug.clients.idea.messages.SamebugBundle;
import com.samebug.clients.idea.messages.SamebugIcons;

public class SamebugToolWindowManager {


    public SamebugToolWindowManager(Project project) {
        this.toolWindowManager = ToolWindowManager.getInstance(project);
        if (toolWindowManager != null) {
            this.toolWindow = createToolWindow(project);
            createToolWindowContent(project);
        } else {
            this.toolWindow = null;
        }
    }

    private ToolWindow createToolWindow(Project project) {
        ToolWindow toolWindow = toolWindowManager.registerToolWindow(SamebugBundle.message("samebug.toolwindow.id"), true, ToolWindowAnchor.RIGHT, project, true);
        toolWindow.setType(ToolWindowType.SLIDING, null);
        toolWindow.setIcon(SamebugIcons.samebugTab);

        toolWindow.setAvailable(true, null);
        return toolWindow;
    }


    public void createToolWindowContent(Project project) {

    }



    private final ToolWindowManager toolWindowManager;
    private final ToolWindow toolWindow;

}