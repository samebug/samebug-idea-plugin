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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;

final class WebResultsTabController implements IWebResultsTab.Listener {
    final static Logger LOGGER = Logger.getInstance(WebResultsTabController.class);
    final SolutionsController controller;

    public WebResultsTabController(final SolutionsController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IdeaListenerService.WebResultsTab, this);
    }


    @Override
    public void moreClicked() {
        LOGGER.debug("more button clicked");
    }
}
