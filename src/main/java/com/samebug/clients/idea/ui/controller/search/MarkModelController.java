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
        if (mySearchId == searchId) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    final HitView hitCard = controller.view.getHitCard(solutionId);
                    if (hitCard != null) {
                        hitCard.markPanel.markButton.setHighlighted(true);
                        hitCard.markPanel.markButton.revalidate();
                        hitCard.markPanel.markButton.repaint();
                    }
                }
            });
        }
    }

    @Override
    public void successPostingMark(final int searchId, final int solutionId, final @NotNull MarkResponse result) {
        if (mySearchId == searchId) {
            final RestHit hit = service.marked(solutionId, result);
            final MarkPanel.Model newModel = hit == null ? null : hitConverter.convertHit(hit);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    final HitView hitCard = view.getHitCard(solutionId);
                    if (hitCard != null && newModel != null) {
                        hitCard.markPanel.finishPostMarkWithSuccess(newModel);
                    }
                }
            });
        }
    }

    @Override
    public void failPostingMark(final int searchId, final int solutionId, final Exception e) {
        if (mySearchId == searchId) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    final HitView hitCard = view.getHitCard(solutionId);
                    if (hitCard != null) {
                        final String errorMessageKey;
                        if (e instanceof BadRequest) {
                            final String markErrorCode = ((BadRequest) e).getRestError().code;
                            if ("ALREADY_MARKED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyMarked";
                            else if ("NOT_YOUR_SEARCH".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourSearch";
                            else errorMessageKey = "samebug.mark.error.unhandledBadRequest";

                        } else {
                            errorMessageKey = "samebug.mark.error.unhandled";
                        }
                        hitCard.markPanel.finishPostMarkWithError(SamebugBundle.message(errorMessageKey));
                    }
                }
            });
        }
    }

    @Override
    public void finishPostingMark(int searchId, int solutionId) {

    }

    @Override
    public void startRetractMark(final int voteId) {
        final RestHit currentHit = service.getHitForVote(voteId);
        final RestHit predictedHit = currentHit == null ? null : RestHits.asUnmarked(currentHit);
        final MarkPanel.Model updatedModel = predictedHit == null ? null : hitConverter.convertHit(predictedHit);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (currentHit != null && updatedModel != null) {
                    final HitView hitCard = view.getHitCard(currentHit.solutionId);
                    if (hitCard != null) {
                        hitCard.markPanel.beginPostMark(updatedModel);
                    }
                }
            }
        });
    }

    @Override
    public void successRetractMark(final int voteId, final MarkResponse result) {
        RestHit oldHit = controller.service.getHitForVote(voteId);
        if (oldHit != null) {
            // TODO too convoluted
            final RestHit hit = controller.service.markRetracted(oldHit.solutionId, result);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    final HitView hitCard = controller.view.getHitCard(hit.solutionId);
                    if (hitCard != null) {
                        hitCard.markPanel.finishPostMarkWithSuccess(hitConverter.convertHit(controller.service.getSolutions().searchGroup, hit));
                    }
                }
            });
        }
    }

    @Override
    public void failRetractMark(final int voteId, final Exception e) {
        final RestHit hit = controller.service.getHitForVote(voteId);
        if (hit != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    final HitView hitCard = controller.view.getHitCard(hit.solutionId);
                    if (hitCard != null) {
                        final String errorMessageKey;
                        if (e instanceof BadRequest) {
                            final String markErrorCode = ((BadRequest) e).getRestError().code;
                            if ("NOT_YOUR_MARK".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourMark";
                            else if ("ALREADY_CANCELLED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyCancelled";
                            else errorMessageKey = "samebug.mark.error.unhandledBadRequest";

                        } else {
                            errorMessageKey = "samebug.mark.error.unhandled";
                        }
                        hitCard.markPanel.finishPostMarkWithError(SamebugBundle.message(errorMessageKey));
                    }
                }
            });
        }
    }

    @Override
    public void finishRetractMark(int voteId) {

    }

}
