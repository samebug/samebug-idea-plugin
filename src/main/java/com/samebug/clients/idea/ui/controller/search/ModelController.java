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
import com.samebug.clients.idea.messages.client.MarkModelListener;
import com.samebug.clients.idea.messages.client.SearchModelListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.card.HitView;
import com.samebug.clients.search.api.entities.MarkResponse;
import com.samebug.clients.search.api.entities.RestHit;
import com.samebug.clients.search.api.entities.Solutions;
import com.samebug.clients.search.api.exceptions.BadRequest;
import org.jetbrains.annotations.NotNull;

public class ModelController implements SearchModelListener, MarkModelListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final SearchTabController controller;
    final int mySearchId;

    public ModelController(@NotNull final SearchTabController controller) {
        this.controller = controller;
        this.mySearchId = controller.mySearchId;

        MessageBusConnection projectMessageBus = controller.project.getMessageBus().connect(controller.project);
        projectMessageBus.subscribe(SearchModelListener.TOPIC, this);
        projectMessageBus.subscribe(MarkModelListener.TOPIC, this);
    }

    @Override
    public void startLoadingSolutions(final int searchId) {
        if (mySearchId == searchId) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    controller.view.setWarningLoading();
                }
            });
        }
    }

    @Override
    public void successLoadingSolutions(final int searchId, final Solutions result) {
        if (mySearchId == searchId) {
            controller.service.setSolutions(result);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    controller.refreshTab();
                }
            });
        }
    }

    @Override
    public void failLoadingSolutions(final int searchId, final java.lang.Exception e) {
        if (mySearchId == searchId) {
            controller.service.setSolutions(null);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    controller.refreshTab();
                }
            });
        }
    }

    @Override
    public void finishLoadingSolutions(final int searchId) {
    }


    @Override
    public void startPostingMark(int searchId, int solutionId) {
        if (mySearchId == searchId) {
            final HitView hitCard = controller.view.getHitCard(solutionId);
            if (hitCard != null) hitCard.markPanel.markButton.setHighlighted(true);
        }
    }

    @Override
    public void successPostingMark(int searchId, int solutionId, MarkResponse result) {
        if (mySearchId == searchId) {
            // TODO too convoluted
            RestHit hit = controller.service.marked(solutionId, result);
            final HitView hitCard = controller.view.getHitCard(solutionId);
            if (hitCard != null) {
                hitCard.markPanel.finishPostMarkWithSuccess(controller.convertHit(controller.service.getSolutions().searchGroup, hit));
            }
        }
    }

    @Override
    public void failPostingMark(int searchId, int solutionId, Exception e) {
        if (mySearchId == searchId) {
            final HitView hitCard = controller.view.getHitCard(solutionId);
            if (hitCard != null) {
                final String errorMessageKey;
                if (e instanceof BadRequest) {
                    final String markErrorCode = ((BadRequest) e).getRestError().code;
                    if ("ALREADY_MARKED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyMarked";
                    else if ("NOT_YOUR_SEARCH".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourSearch";
//                    else if ("NOT_YOUR_MARK".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourMark";
//                    else if ("ALREADY_CANCELLED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyCancelled";
                    else errorMessageKey = "samebug.mark.error.unhandledBadRequest";

                } else {
                    errorMessageKey = "samebug.mark.error.unhandled";
                }
                hitCard.markPanel.finishPostMarkWithError(SamebugBundle.message(errorMessageKey));
            }
        }
    }

    @Override
    public void finishPostingMark(int searchId, int solutionId) {

    }

    @Override
    public void startRetractMark(int voteId) {
        // TODO
    }

    @Override
    public void successRetractMark(int voteId, MarkResponse result) {
        // TODO
    }

    @Override
    public void failRetractMark(int voteId, Exception e) {
        // TODO
    }

    @Override
    public void finishRetractMark(int voteId) {

    }
}
