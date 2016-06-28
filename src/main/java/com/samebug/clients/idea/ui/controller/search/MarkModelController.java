package com.samebug.clients.idea.ui.controller.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.services.RestHits;
import com.samebug.clients.common.services.SearchService;
import com.samebug.clients.idea.messages.client.MarkModelListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.card.HitView;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.component.tab.SearchTabView;
import com.samebug.clients.search.api.entities.MarkResponse;
import com.samebug.clients.search.api.entities.RestHit;
import com.samebug.clients.search.api.exceptions.BadRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handling model change events related to marks.
 *
 * Post and retract of marks happen similarly:
 *  - when the process starts, the outcome is predicted optimistically, and the view is updated as if the process happened successfully
 *  - if it succeeds, the model is updated with the result, and the view is also updated with the exact result
 *  - if it fails, the view is updated to reflect the unchanged model.
 *
 * Notes:
 *  - these events have to come from worker threads, any ui access should be done separately
 *  - there could be more instances of this class, and it's possible that a received event is not related to this controller
 *
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

        MessageBusConnection projectMessageBus = controller.project.getMessageBus().connect(controller);
        projectMessageBus.subscribe(MarkModelListener.TOPIC, this);
    }

    @Override
    public void startPostingMark(final int searchId, final int solutionId) {
        final RestHit currentHit = service.getHit(searchId, solutionId);
        final RestHit predictedHit = currentHit == null ? null : RestHits.asMarked(currentHit);
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
        final RestHit predictedHit = currentHit == null ? null : RestHits.asUnmarked(currentHit);
        updateViewForStartedMark(currentHit, predictedHit);
    }

    @Override
    public void successRetractMark(final int voteId, final MarkResponse result) {
        final RestHit oldHit = controller.service.getHitForVote(voteId);
        if (oldHit != null) {
            final RestHit updatedHit = service.unmarked(oldHit.solutionId, result);
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
        final MarkPanel.Model updatedModel = predictedHit == null ? null : hitConverter.convertHit(predictedHit);
        if (currentHit != null && updatedModel != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    final HitView hitCard = view.getHitCard(currentHit.solutionId);
                    if (hitCard != null) {
                        hitCard.markPanel.beginPostMark(updatedModel);
                    }
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
                    final HitView hitCard = view.getHitCard(updatedHit.solutionId);
                    if (hitCard != null) {
                        hitCard.markPanel.finishPostMarkWithSuccess(updatedModel);
                    }
                }
            });
        }
    }

    private void updateViewForFailedMark(@Nullable final RestHit currentHit, @NotNull final Exception e) {
        if (currentHit != null) {
            final String errorMessageKey;
            if (e instanceof BadRequest) {
                final String markErrorCode = ((BadRequest) e).getRestError().code;
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
                    final HitView hitCard = controller.view.getHitCard(currentHit.solutionId);
                    if (hitCard != null) {
                        hitCard.markPanel.finishPostMarkWithError(SamebugBundle.message(errorMessageKey));
                    }
                }
            });
        }
    }
}
