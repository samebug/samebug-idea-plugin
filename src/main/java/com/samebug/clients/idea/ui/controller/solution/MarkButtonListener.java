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

import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.http.entities.mark.NewMark;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.idea.ui.controller.form.CancelMarkFormHandler;
import com.samebug.clients.idea.ui.controller.form.CreateMarkFormHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MarkButtonListener implements IMarkButton.Listener {
    @NotNull
    final SolutionFrameController controller;

    MarkButtonListener(@NotNull final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void markClicked(@NotNull final IMarkButton markButton, @NotNull final Integer solutionId, @Nullable final Integer markId) {
        if (markId == null) {

            new CreateMarkFormHandler(controller.view, markButton, new NewMark(solutionId), controller.searchId) {
                @Override
                protected void afterPostForm(SearchHit response) {
                    if (response != null) {
                        final IMarkButton.Model newModel = controller.conversionService.convertMarkPanel(response, false);
                        markButton.update(newModel);
                    }
                }
            }.execute();
        } else {
            new CancelMarkFormHandler(controller.view, markButton, markId, controller.searchId) {
                @Override
                protected void afterPostForm(SearchHit response) {
                    if (response != null) {
                        final IMarkButton.Model newModel = controller.conversionService.convertMarkPanel(response, false);
                        markButton.update(newModel);
                    }
                }
            }.execute();
        }
    }
}
