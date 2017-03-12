package com.samebug.clients.idea.ui.controller.tipRequestList;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.frame.tipRequestList.ITipRequestListFrame;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.controller.frame.ConnectionStatusController;
import com.samebug.clients.idea.ui.modules.IdeaDataService;
import com.samebug.clients.swing.ui.frame.tipRequestList.TipRequestListFrame;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class TipRequestListController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(TipRequestListController.class);
    final ToolWindowController twc;
    final Project myProject;
    final ITipRequestListFrame view;

    final ConnectionStatusController connectionStatusController;


    public TipRequestListController(ToolWindowController twc, Project project) {
        this.twc = twc;
        this.myProject = project;
        view = new TipRequestListFrame();
        DataService.putData((TipRequestListFrame) view, IdeaDataService.Project, project);

        MessageBus messageBus = myProject.getMessageBus();
        connectionStatusController = new ConnectionStatusController(view, messageBus);

    }

    @NotNull
    public JComponent getControlPanel() {
        return (TipRequestListFrame) view;
    }

    @Override
    public void dispose() {
        connectionStatusController.dispose();
    }

}
