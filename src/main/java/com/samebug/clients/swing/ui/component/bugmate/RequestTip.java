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

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.bugmate.IBugmateList;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public final class RequestTip extends JComponent {
    final BugmateList bugmateList;

    private RequestTipCTAScreen ctaScreen;
    private RequestTipScreen tipScreen;

    public RequestTip(BugmateList bugmateList) {
        this.bugmateList = bugmateList;
        setLayout(new MigLayout("fillx", "0[fill]0", "0[fill]0"));
        changeToClosedState();
    }

    public void startRequestTip() {
        // TODO loading
        if (tipScreen == null) return;
        tipScreen.sendButton.setText("loading...");
    }

    public void failRequestTip(List<FieldError> errors) throws FormMismatchException {
        if (tipScreen != null) tipScreen.setFormErrors(errors);
    }

    public void successRequestTip() {
        // TODO keep track of open/close state in a boolean?
        if (ctaScreen == null) changeToClosedState();
    }

    void changeToOpenState() {
        assert tipScreen == null : "Tip screen should not be open";
        tipScreen = new RequestTipScreen(bugmateList);
        removeAll();
        ctaScreen = null;
        add(tipScreen);

        revalidate();
        repaint();
    }

    void changeToClosedState() {
        assert ctaScreen == null : "CTA screen should not be open";
        ctaScreen = new RequestTipCTAScreen(bugmateList);
        removeAll();
        tipScreen = null;
        add(ctaScreen);

        revalidate();
        repaint();
    }

    IBugmateList.Listener getListener() {
        return ListenerService.getListener(this, IBugmateList.Listener.class);
    }

}
