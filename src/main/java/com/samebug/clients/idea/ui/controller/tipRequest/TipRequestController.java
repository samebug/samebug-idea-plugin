package com.samebug.clients.idea.ui.controller.tipRequest;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.frame.tipRequest.ITipRequestFrame;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.controller.frame.ConnectionStatusController;
import com.samebug.clients.idea.ui.modules.IdeaDataService;
import com.samebug.clients.swing.ui.frame.solution.SolutionFrame;
import com.samebug.clients.swing.ui.frame.tipRequest.TipRequestFrame;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class TipRequestController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(TipRequestController.class);
    final ToolWindowController twc;
    final Project myProject;
    final ITipRequestFrame view;

    final ConnectionStatusController connectionStatusController;


    public TipRequestController(ToolWindowController twc, Project project) {
        this.twc = twc;
        this.myProject = project;
        view = new TipRequestFrame();
        DataService.putData((TipRequestFrame) view, IdeaDataService.Project, project);

        MessageBus messageBus = myProject.getMessageBus();
        connectionStatusController = new ConnectionStatusController(view, messageBus);

    }

    public int getTipRequestId() {
        // TODO
        return 0;
    }


    @NotNull
    public JComponent getControlPanel() {
        return (TipRequestFrame) view;
    }

    @Override
    public void dispose() {
        connectionStatusController.dispose();
    }

}
