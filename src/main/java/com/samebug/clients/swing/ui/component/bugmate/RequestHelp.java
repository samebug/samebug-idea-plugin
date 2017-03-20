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
import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public final class RequestHelp extends JComponent implements IAskForHelp {
    final Model model;

    private RequestHelpCTAScreen ctaScreen;
    private RequestHelpScreen tipScreen;

    public RequestHelp(Model model) {
        this.model = model;
        setLayout(new MigLayout("fillx", "0[fill]0", "0[fill]0"));
        changeToClosedState();
    }

    @Override
    public void startRequestTip() {
        if (tipScreen == null) return;
        tipScreen.sendButton.changeToLoadingAnimation();
    }

    @Override
    public void failRequestTip(List<FieldError> errors) throws FormMismatchException {
        if (tipScreen != null) {
            tipScreen.setFormErrors(errors);
            tipScreen.sendButton.revertFromLoadingAnimation();
        }
    }

    @Override
    public void successRequestTip() {
        if (ctaScreen == null) changeToClosedState();
    }

    void changeToOpenState() {
        assert tipScreen == null : "Tip screen should not be open";
        tipScreen = new RequestHelpScreen(this);
        removeAll();
        ctaScreen = null;
        add(tipScreen);

        revalidate();
        repaint();
    }

    void changeToClosedState() {
        assert ctaScreen == null : "CTA screen should not be open";
        ctaScreen = new RequestHelpCTAScreen(this);
        removeAll();
        tipScreen = null;
        add(ctaScreen);

        revalidate();
        repaint();
    }

    Listener getListener() {
        return ListenerService.getListener(this, IAskForHelp.Listener.class);
    }
}
