package com.samebug.clients.idea.ui.controller.authentication;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.ui.component.authentication.IAuthenticationFrame;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.swing.ui.component.authentication.AuthenticationFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class AuthenticationController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(AuthenticationController.class);
    final ToolWindowController twc;
    final Project myProject;

    final IAuthenticationFrame view;

    public AuthenticationController(ToolWindowController twc, Project project) {
        this.twc = twc;
        this.myProject = project;

        view = new AuthenticationFrame();
    }

    @NotNull
    public JComponent getControlPanel() {
        return (AuthenticationFrame) view;
    }

    @Override
    public void dispose() {

    }
}
