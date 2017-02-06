package com.samebug.clients.idea.ui.controller.intro;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.project.ToolWindowController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class IntroFrameController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(IntroFrameController.class);
    @NotNull
    final ToolWindowController twc;
    @NotNull
    final Project project;

    @NotNull
    final JPanel view;

    public IntroFrameController(@NotNull ToolWindowController twc, @NotNull Project project) {
        this.twc = twc;
        this.project = project;
        this.view = new JPanel();
    }

    @NotNull
    public JPanel getControlPanel() {
        return view;
    }

    @Override
    public void dispose() {

    }
}
