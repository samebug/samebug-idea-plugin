/*
 * Copyright 2018 Samebug, Inc.
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

import com.samebug.clients.common.ui.frame.solution.ISearchHeaderPanel;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.modules.BrowserUtil;
import com.samebug.clients.swing.tracking.SwingRawEvent;

import javax.swing.*;
import java.net.URI;

final class ExceptionHeaderListener implements ISearchHeaderPanel.Listener {
    final SolutionFrameController controller;

    ExceptionHeaderListener(final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void titleClicked(ISearchHeaderPanel source) {
        final URI searchUri = IdeaSamebugPlugin.getInstance().uriBuilder.search(controller.searchId);
        BrowserUtil.browse(searchUri);
        TrackingService.trace(SwingRawEvent.linkClick((JComponent) source, searchUri));
    }
}
