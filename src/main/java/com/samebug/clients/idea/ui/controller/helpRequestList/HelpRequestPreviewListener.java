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
package com.samebug.clients.idea.ui.controller.helpRequestList;

import com.samebug.clients.common.ui.component.helpRequest.IHelpRequestPreview;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.modules.DataService;

import javax.swing.*;

final class HelpRequestPreviewListener implements IHelpRequestPreview.Listener {

    final HelpRequestListController controller;

    HelpRequestPreviewListener(HelpRequestListController controller) {
        this.controller = controller;
    }


    @Override
    public void previewClicked(IHelpRequestPreview source, String helpRequestId) {
        final String transactionId = DataService.getData((JComponent) source, TrackingKeys.WriteTipTransaction);
        controller.twc.focusOnHelpRequest(helpRequestId, transactionId);
    }
}
