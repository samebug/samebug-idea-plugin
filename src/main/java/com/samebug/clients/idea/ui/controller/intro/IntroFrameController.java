package com.samebug.clients.idea.ui.controller.intro;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

// TODO
public class IntroFrameController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(IntroFrameController.class);
    @NotNull
    final ToolWindowController twc;
    @NotNull
    final Project project;

    @NotNull
    final JComponent view;

    public IntroFrameController(@NotNull ToolWindowController twc, @NotNull Project project) {
        this.twc = twc;
        this.project = project;
        this.view = new SamebugLabel("TODO intro panel");
    }

    @NotNull
    public JComponent getControlPanel() {
        return view;
    }

    @Override
    public void dispose() {

    }
}
