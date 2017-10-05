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

import com.samebug.clients.common.tracking.Funnels;
import com.samebug.clients.common.ui.component.community.IAskForHelpViaChat;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public final class RequestHelpViaChat extends JComponent implements IAskForHelpViaChat {
    final Model model;

    private RequestHelpViaChatScreen ctaScreen;

    public RequestHelpViaChat(Model model) {
        DataService.putData(this, TrackingKeys.HelpRequestTransaction, Funnels.newTransactionId());
        this.model = model;
        setLayout(new MigLayout("fillx", "0[fill]0", "0[fill]0"));
        changeToClosedState();
    }

    @Override
    public void startChat() {
        ctaScreen.askButton.changeToLoadingAnimation();
    }

    @Override
    public void failStartChat(@Nullable final BadRequest errors) {
        if (errors != null) {
            // TODO handle errors when we have them defined
        }
        ctaScreen.askButton.revertFromLoadingAnimation();
    }

    @Override
    public void successStartChat() {
        ctaScreen.askButton.revertFromLoadingAnimation();
    }

    void changeToClosedState() {
        assert ctaScreen == null : "CTA screen should not be open";
        ctaScreen = new RequestHelpViaChatScreen(this);
        removeAll();
        add(ctaScreen);

        revalidate();
        repaint();
    }

    Listener getListener() {
        return ListenerService.getListener(this, IAskForHelpViaChat.Listener.class);
    }
}
