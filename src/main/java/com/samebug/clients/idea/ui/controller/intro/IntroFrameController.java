package com.samebug.clients.idea.ui.controller.intro;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.intro.IIntroFrame;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.controller.ConnectionStatusController;
import com.samebug.clients.swing.ui.component.intro.IntroFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

// TODO
public class IntroFrameController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(IntroFrameController.class);
    final ToolWindowController twc;
    final Project myProject;
    final ConnectionStatusController connectionStatusController;

    @NotNull
    final IIntroFrame view;

    public IntroFrameController(ToolWindowController twc, Project project) {
        this.twc = twc;
        this.myProject = project;
        this.view = new IntroFrame();

        MessageBus messageBus = myProject.getMessageBus();
        connectionStatusController = new ConnectionStatusController(view, messageBus);
    }

    @NotNull
    public JComponent getControlPanel() {
        return (IntroFrame) view;
    }

    @Override
    public void dispose() {
        connectionStatusController.dispose();
    }
}
