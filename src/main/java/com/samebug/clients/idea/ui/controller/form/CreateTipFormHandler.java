/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.controller.form;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.http.entities.search.NewSearchHit;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.TipCreate;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CreateTipFormHandler extends PostFormHandler<SearchHit<SamebugTip>, TipCreate.BadRequest> {
    private static final Logger LOGGER = Logger.getInstance(CreateTipFormHandler.class);

    @NotNull
    final NewSearchHit data;
    @NotNull
    final Integer searchId;

    public CreateTipFormHandler(@NotNull final NewSearchHit data, @NotNull final Integer searchId) {
        this.data = data;
        this.searchId = searchId;
    }

    @NotNull
    @Override
    protected final SearchHit<SamebugTip> postForm() throws SamebugClientException, TipCreate.BadRequest {
        final SolutionService solutionService = IdeaSamebugPlugin.getInstance().solutionService;
        return solutionService.postTip(searchId, data);
    }

    @Override
    protected final void afterPostForm(@NotNull SearchHit<SamebugTip> response) {
        ITipHit.Model tip = IdeaSamebugPlugin.getInstance().conversionService.tipHit(response, false);
        afterPostFormUI(tip);
    }

    @Override
    protected final void handleBadRequest(@NotNull final TipCreate.BadRequest fieldErrors) {
        IHelpOthersCTA.BadRequest error = null;
        IHelpOthersCTA.BadRequest.TipBody tipBody = null;
        for (TipCreate.ErrorCode errorCode : fieldErrors.errorList.getErrorCodes()) {
            switch (errorCode) {
                case MESSAGE_TOO_SHORT:
                    tipBody = IHelpOthersCTA.BadRequest.TipBody.TOO_SHORT;
                    break;
                case MESSAGE_TOO_LONG:
                    tipBody = IHelpOthersCTA.BadRequest.TipBody.TOO_LONG;
                    break;
                default:
                    LOGGER.warn("Unhandled error code " + errorCode);
            }
        }
        if (tipBody != null) error = new IHelpOthersCTA.BadRequest(tipBody);
        handleBadRequestUI(error);
    }

    protected abstract void afterPostFormUI(@NotNull ITipHit.Model tip);

    protected abstract void handleBadRequestUI(@Nullable IHelpOthersCTA.BadRequest errors);

}
