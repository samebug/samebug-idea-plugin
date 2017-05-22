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
package com.samebug.clients.swing.ui.component.bugmate;

import com.samebug.clients.common.ui.component.helpRequest.IMyHelpRequest;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.common.ui.modules.TextService;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.label.TimestampLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

public final class RevokeHelpRequest extends JComponent implements IMyHelpRequest {
    private final JComponent helpRequestLabel;
    private final SamebugButton revoke;

    public RevokeHelpRequest(Model model) {
        helpRequestLabel = new HelpRequestBar(model);
        revoke = new RevokeButton(model);

        setLayout(new MigLayout("fillx", "0[]0", "0[]15px[]0"));
        add(helpRequestLabel, "cell 0 0, growx");
        add(revoke, "cell 0 1, align center");
    }

    @Override
    public void startRevoke() {
        revoke.changeToLoadingAnimation();
    }

    @Override
    public void failRevoke() {
        revoke.revertFromLoadingAnimation();
    }

    @Override
    public void successRevoke() {
        revoke.revertFromLoadingAnimation();
    }

    private final class HelpRequestBar extends SamebugPanel {
        HelpRequestBar(Model helpRequest) {
            final SamebugLabel text = new HelpRequestLabel(helpRequest.createdAt);
            setBackgroundColor(ColorService.Tip);

            setLayout(new MigLayout("fillx", "30px[fill]30px", "30px[]30px"));
            add(text, "al center");
        }
    }

    private final class RevokeButton extends SamebugButton {
        RevokeButton(final Model helpRequest) {
            setText(MessageService.message("samebug.component.helpRequest.ask.revoke"));
            DataService.putData(this, TrackingKeys.Label, getText());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        getListener().revokeHelpRequest(RevokeHelpRequest.this, helpRequest.id);
                        TrackingService.trace(SwingRawEvent.buttonClick(RevokeButton.this));
                    }
                }
            });
        }
    }

    private final class HelpRequestLabel extends SamebugLabel implements TimestampLabel {
        private final Date timestamp;

        HelpRequestLabel(Date timestamp) {
            this.timestamp = timestamp;
            setFont(FontService.regular(16));
            setForegroundColor(ColorService.TipText);
            setHorizontalAlignment(SwingConstants.CENTER);
            updateRelativeTimestamp();
        }

        @Override
        public void updateRelativeTimestamp() {
            // TODO it is not exactly the same as in the design
            String date = TextService.adaptiveTimestamp(timestamp);
            setText(MessageService.message("samebug.component.helpRequest.ask.alreadySent", date));
        }
    }

    Listener getListener() {
        return ListenerService.getListener(this, IMyHelpRequest.Listener.class);
    }

}
