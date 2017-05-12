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
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.MarkCancel;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;
import org.jetbrains.annotations.NotNull;

public abstract class CancelMarkFormHandler extends PostFormHandler<Mark, MarkCancel.BadRequest> {
    @NotNull
    final IFrame frame;
    @NotNull
    final IMarkButton button;
    @NotNull
    final Integer markId;

    public CancelMarkFormHandler(@NotNull final IFrame frame, @NotNull final IMarkButton button, @NotNull final Integer markId) {
        this.frame = frame;
        this.button = button;
        this.markId = markId;
    }

    @Override
    protected void beforePostForm() {
        button.setLoading();
    }

    @NotNull
    @Override
    protected Mark postForm() throws SamebugClientException, MarkCancel.BadRequest {
        final SolutionService solutionService = IdeaSamebugPlugin.getInstance().solutionService;
        return solutionService.retractMark(markId);
    }

    @Override
    protected void handleBadRequest(@NotNull MarkCancel.BadRequest fieldErrors) {
        frame.popupError(MessageService.message("samebug.component.mark.cancel.error.unhandled"));
        button.interruptLoading();
    }

    @Override
    protected void handleOtherClientExceptions(@NotNull SamebugClientException exception) {
        frame.popupError(MessageService.message("samebug.component.mark.cancel.error.unhandled"));
        button.interruptLoading();
    }
}
