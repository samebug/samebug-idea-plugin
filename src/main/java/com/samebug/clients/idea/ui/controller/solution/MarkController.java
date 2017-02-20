package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.entities.MarkResponse;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.solutions.IMarkButton;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;

final class MarkController implements IMarkButton.Listener {
    final static Logger LOGGER = Logger.getInstance(MarkController.class);
    final SolutionFrameController controller;

    public MarkController(final SolutionFrameController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IMarkButton.Listener.TOPIC, this);
    }

    @Override
    public void markClicked(final IMarkButton markButton, final Integer solutionId, final Integer markId) {
        markButton.setLoading();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                SolutionService solutionService = IdeaSamebugPlugin.getInstance().getSolutionService();
                try {
                    final IMarkButton.Model newModel;
                    if (markId == null) {
                        final MarkResponse response = solutionService.postMark(controller.searchId, solutionId);
                        newModel = controller.convertMarkResponse(response);
                    } else {
                        final MarkResponse response = solutionService.retractMark(markId);
                        newModel = controller.convertRetractedMarkResponse(response);
                    }
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            markButton.update(newModel);
                        }
                    });
                } catch (SamebugClientException e) {
                    LOGGER.warn("Mark failed", e);
                    markButton.setError();
                }
            }
        });
    }
}