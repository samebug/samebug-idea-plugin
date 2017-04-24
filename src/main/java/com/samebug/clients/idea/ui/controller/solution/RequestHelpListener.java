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

import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.http.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.http.form.HelpRequestCreate;
import com.samebug.clients.idea.ui.controller.form.CreateHelpRequestFormHandler;

final class RequestHelpListener implements IAskForHelp.Listener {
    final SolutionFrameController controller;

    public RequestHelpListener(final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void askBugmates(final IAskForHelp source, final String description) {
        new CreateHelpRequestFormHandler(controller.view, source, new HelpRequestCreate.Data(controller.searchId, description)) {
            @Override
            protected void afterPostForm(MyHelpRequest response) {
                controller.load();
            }
        }.execute();
    }
}
