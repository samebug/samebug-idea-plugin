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
package com.samebug.clients.idea.ui.controller.popupController;

import com.samebug.clients.common.tracking.Hooks;
import com.samebug.clients.common.ui.component.popup.IHelpRequestPopup;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.entities.notification.IncomingHelpRequest;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.modules.DataService;

import javax.swing.*;

public final class HelpRequestPopupListener implements IHelpRequestPopup.Listener {
    final HelpRequestPopupController controller;

    public HelpRequestPopupListener(HelpRequestPopupController controller) {
        this.controller = controller;
    }

    @Override
    public void answerClick(IHelpRequestPopup source) {
        IncomingHelpRequest helpRequest = controller.data.get(source);
        assert helpRequest != null;

        final String helpRequestId = helpRequest.getMatch().getHelpRequest().getId();
        final JComponent popup = (JComponent) source;
        final String transactionId = DataService.getData(popup, TrackingKeys.WriteTipTransaction);
        controller.twc.focusOnHelpRequest(helpRequestId, transactionId);
        controller.hideAndRemoveIncomingHelpRequest(source);
        TrackingService.trace(SwingRawEvent.writeTipHookTrigger(popup, transactionId, helpRequestId, Hooks.WriteTip.HELP_REQUEST_RESPONSE));
    }

    @Override
    public void laterClick(IHelpRequestPopup source) {
        controller.hideAndRemoveIncomingHelpRequest(source);
    }
}
