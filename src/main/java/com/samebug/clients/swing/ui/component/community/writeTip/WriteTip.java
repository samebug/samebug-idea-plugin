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
package com.samebug.clients.swing.ui.component.community.writeTip;

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public final class WriteTip extends JComponent implements IHelpOthersCTA {
    final IHelpOthersCTA.Model model;
    final CTA_TYPE ctaType;

    private WriteTipCTAScreen ctaScreen;
    private WriteTipScreen tipScreen;

    public WriteTip(IHelpOthersCTA.Model model, CTA_TYPE ctaType) {
        this.model = new IHelpOthersCTA.Model(model);
        this.ctaType = ctaType;

        setLayout(new MigLayout("fillx", "0[fill]0", "0[fill]0"));
        changeToClosedState();
    }

    @Override
    public void startPostTip() {
        if (tipScreen == null) return;
        tipScreen.actionRow.sendButton.changeToLoadingAnimation();
    }

    @Override
    public void successPostTip() {
        // TODO do we have to properly close the loading animation on the send button?
        if (ctaScreen == null) changeToClosedState();
    }

    @Override
    public void failPostTipWithFormError(List<FieldError> errors) throws FormMismatchException {
        if (tipScreen != null) {
            tipScreen.actionRow.sendButton.revertFromLoadingAnimation();
            tipScreen.setFormErrors(errors);
        }
    }

    public enum CTA_TYPE {
        SMALL, LARGE_FOR_TIP_HITS, LARGE_FOR_WEB_HITS
    }

    void changeToOpenState() {
        assert tipScreen == null : "Tip screen should not be open";
        tipScreen = new WriteTipScreen(this);
        removeAll();
        ctaScreen = null;
        add(tipScreen);
        tipScreen.tipArea.borderedArea.requestFocus();

        revalidate();
        repaint();
    }

    void changeToClosedState() {
        assert ctaScreen == null : "CTA screen should not be open";
        ctaScreen = createCTAScreen();
        removeAll();
        tipScreen = null;
        add(ctaScreen.getCTAScreen());

        ctaScreen.getCTAButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeToOpenState();
            }
        });

        revalidate();
        repaint();
    }

    @NotNull
    private WriteTipCTAScreen createCTAScreen() {
        WriteTipCTAScreen s = null;
        switch (ctaType) {
            case SMALL:
                s = new SmallWriteTipCTAScreen(model.usersWaitingHelp);
                break;
            case LARGE_FOR_TIP_HITS:
                s = new LargeWriteTipCTAScreen();
                String l1 = MessageService.message("samebug.component.cta.writeTip.noTipHits.label", model.usersWaitingHelp);
                ((LargeWriteTipCTAScreen) s).label.setText(l1);
                break;
            case LARGE_FOR_WEB_HITS:
                s = new LargeWriteTipCTAScreen();
                String l2 = MessageService.message("samebug.component.cta.writeTip.noWebHits.label", model.usersWaitingHelp);
                ((LargeWriteTipCTAScreen) s).label.setText(l2);
                break;
            default:
        }
        return s;
    }

    Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }

}
