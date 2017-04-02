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
package com.samebug.clients.idea.ui.controller.solution;

import com.samebug.clients.common.api.entities.solution.MarkResponse;
import com.samebug.clients.common.api.form.CancelMark;
import com.samebug.clients.common.api.form.CreateMark;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.idea.ui.controller.form.CancelMarkFormHandler;
import com.samebug.clients.idea.ui.controller.form.CreateMarkFormHandler;

final class MarkButtonListener implements IMarkButton.Listener {
    final SolutionFrameController controller;

    public MarkButtonListener(final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void markClicked(final IMarkButton markButton, final Integer solutionId, final Integer markId) {
        if (markId == null) {

            new CreateMarkFormHandler(controller.view, markButton, new CreateMark(controller.searchId, solutionId)) {
                @Override
                protected void afterPostForm(MarkResponse response) {
                    final IMarkButton.Model newModel = controller.conversionService.convertMarkResponse(response);
                    markButton.update(newModel);
                }
            }.execute();
        } else {
            new CancelMarkFormHandler(controller.view, markButton, new CancelMark(markId)) {
                @Override
                protected void afterPostForm(MarkResponse response) {
                    final IMarkButton.Model newModel = controller.conversionService.convertRetractedMarkResponse(response);
                    markButton.update(newModel);
                }
            }.execute();
        }
    }
}