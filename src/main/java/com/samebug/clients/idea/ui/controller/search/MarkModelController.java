/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.controller.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.entities.MarkResponse;
import com.samebug.clients.common.search.api.entities.RestHit;
import com.samebug.clients.common.search.api.exceptions.BadRequest;
import com.samebug.clients.common.services.SearchService;
import com.samebug.clients.idea.messages.client.MarkModelListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.component.tab.SearchTabView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handling model change events related to marks.
 * <p>
 * Post and retract of marks happen similarly:
 * - when the process starts, the outcome is predicted optimistically, and the view is updated as if the process happened successfully
 * - if it succeeds, the model is updated with the result, and the view is also updated with the exact result
 * - if it fails, the view is updated to reflect the unchanged model.
 * <p>
 * Notes:
 * - these events have to come from worker threads, any ui access should be done separately
 * - there could be more instances of this class, and it's possible that a received event is not related to this controller
 */
final class MarkModelController implements MarkModelListener {
    final static Logger LOGGER = Logger.getInstance(MarkModelController.class);
    @NotNull
    final SearchTabController controller;
    @NotNull
    final HitConverter hitConverter;
    @NotNull
    final SearchTabView view;
    @NotNull
    final SearchService service;
    @NotNull
    final int mySearchId;

    public MarkModelController(@NotNull final SearchTabController controller) {
        this.controller = controller;
        this.hitConverter = controller.hitConverter;
        this.view = controller.view;
        this.service = controller.service;
        this.mySearchId = controller.mySearchId;

        MessageBusConnection projectConnection = controller.project.getMessageBus().connect(controller);
        projectConnection.subscribe(MarkModelListener.TOPIC, this);
    }

    @Override
    public void startPostingMark(final int searchId, final int solutionId) {
        final RestHit currentHit = service.getHit(searchId, solutionId);
        final RestHit predictedHit = currentHit == null ? null : currentHit.asMarked();
        updateViewForStartedMark(currentHit, predictedHit);
    }


    @Override
    public void successPostingMark(final int searchId, final int solutionId, final @NotNull MarkResponse result) {
        if (mySearchId == searchId) {
            final RestHit updatedHit = service.marked(solutionId, result);
            updateViewForFinishedMark(updatedHit);
        }
    }

    @Override
    public void failPostingMark(final int searchId, final int solutionId, final Exception e) {
        final RestHit currentHit = controller.service.getHit(searchId, solutionId);
        updateViewForFailedMark(currentHit, e);
    }

    @Override
    public void finishPostingMark(int searchId, int solutionId) {

    }

    @Override
    public void startRetractMark(final int voteId) {
        final RestHit currentHit = service.getHitForVote(voteId);
        final RestHit predictedHit = currentHit == null ? null : currentHit.asUnmarked();
        updateViewForStartedMark(currentHit, predictedHit);
    }

    @Override
    public void successRetractMark(final int voteId, final MarkResponse result) {
        final RestHit oldHit = controller.service.getHitForVote(voteId);
        if (oldHit != null) {
            final RestHit updatedHit = service.unmarked(oldHit.getSolutionId(), result);
            updateViewForFinishedMark(updatedHit);
        }
    }

    @Override
    public void failRetractMark(final int voteId, final Exception e) {
        final RestHit currentHit = controller.service.getHitForVote(voteId);
        updateViewForFailedMark(currentHit, e);
    }

    @Override
    public void finishRetractMark(int voteId) {

    }


    private void updateViewForStartedMark(@Nullable final RestHit currentHit, @Nullable final RestHit predictedHit) {
        final MarkPanel.Model predictedModel = predictedHit == null ? null : hitConverter.convertHit(predictedHit);
        if (currentHit != null && predictedModel != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.beginPostMark(currentHit.getSolutionId(), predictedModel);
                }
            });
        }
    }

    private void updateViewForFinishedMark(@Nullable final RestHit updatedHit) {
        final MarkPanel.Model updatedModel = updatedHit == null ? null : hitConverter.convertHit(updatedHit);
        if (updatedHit != null && updatedModel != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.finishPostMarkWithSuccess(updatedHit.getSolutionId(), updatedModel);
                }
            });
        }
    }

    private void updateViewForFailedMark(@Nullable final RestHit currentHit, @NotNull final Exception e) {
        if (currentHit != null) {
            final String errorMessageKey;
            if (e instanceof BadRequest) {
                final String markErrorCode = ((BadRequest) e).getRestError().getCode();
                if ("ALREADY_MARKED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyMarked";
                else if ("NOT_YOUR_SEARCH".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourSearch";
                else if ("NOT_YOUR_MARK".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourMark";
                else if ("ALREADY_CANCELLED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyCancelled";
                else errorMessageKey = "samebug.mark.error.unhandledBadRequest";
            } else {
                errorMessageKey = "samebug.mark.error.unhandled";
            }
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.finishPostMarkWithError(currentHit.getSolutionId(), SamebugBundle.message(errorMessageKey));
                }
            });
        }
    }
}
