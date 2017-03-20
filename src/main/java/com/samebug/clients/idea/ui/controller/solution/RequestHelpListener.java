package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.common.api.form.CreateHelpRequest;
import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.idea.ui.controller.form.CreateHelpRequestFormHandler;

final class RequestHelpListener implements IAskForHelp.Listener {
    final static Logger LOGGER = Logger.getInstance(RequestHelpListener.class);

    final SolutionFrameController controller;

    public RequestHelpListener(final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void askBugmates(final IAskForHelp source, final String description) {
        new CreateHelpRequestFormHandler(controller.view, source, new CreateHelpRequest(controller.searchId, description)) {
            @Override
            protected void afterPostForm(MyHelpRequest response) {
                controller.load();
            }
        }.execute();
    }
}
