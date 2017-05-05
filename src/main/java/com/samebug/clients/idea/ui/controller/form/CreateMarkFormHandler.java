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

import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.http.entities.mark.Mark;
import com.samebug.clients.http.entities.mark.NewMark;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.MarkCreate;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;
import org.jetbrains.annotations.NotNull;

public abstract class CreateMarkFormHandler extends PostFormHandler<Mark, MarkCreate.BadRequest> {
    @NotNull
    final IFrame frame;
    @NotNull
    final IMarkButton button;
    @NotNull
    final NewMark data;
    @NotNull
    final Integer searchId;

    public CreateMarkFormHandler(@NotNull final IFrame frame, @NotNull final IMarkButton button, @NotNull final NewMark data, @NotNull final Integer searchId) {
        this.frame = frame;
        this.button = button;
        this.data = data;
        this.searchId = searchId;
    }

    @Override
    protected void beforePostForm() {
        button.setLoading();
    }

    @Override
    protected Mark postForm() throws SamebugClientException, MarkCreate.BadRequest {
        final SolutionService solutionService = IdeaSamebugPlugin.getInstance().solutionService;
        return solutionService.postMark(searchId, data);
    }

    @Override
    protected void handleBadRequest(MarkCreate.BadRequest fieldErrors) {
//        if (nonFormError.code.equals(CreateMark.E_ALREADY_MARKED)) globalErrors.add(MessageService.message("samebug.component.mark.cancel.error.alreadyCancelled"));
        button.interruptLoading();
    }

    @Override
    protected void handleOtherClientExceptions(SamebugClientException exception) {
        frame.popupError(MessageService.message("samebug.component.mark.create.error.unhandled"));
        button.interruptLoading();
    }
}
