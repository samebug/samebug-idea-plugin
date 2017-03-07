package com.samebug.clients.swing.ui.component.solutions.requestTip;

import com.samebug.clients.common.ui.component.solutions.IRequestTip;
import com.samebug.clients.swing.ui.component.solutions.writeTip.WriteTip;
import com.samebug.clients.swing.ui.component.solutions.writeTip.WriteTipCTAScreen;
import com.samebug.clients.swing.ui.component.solutions.writeTip.WriteTipScreen;
import com.samebug.clients.swing.ui.global.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class RequestTip extends JComponent implements IRequestTip {
    private RequestTipCTAScreen ctaScreen;
    private RequestTipScreen tipScreen;

    public RequestTip() {
        setLayout(new MigLayout("fillx", "0[fill]0", "0[fill]0"));
        changeToClosedState();
    }

    @Override
    public void startRequestTip() {

    }

    @Override
    public void interruptRequestTip() {

    }

    @Override
    public void successRequestTip() {

    }

    void changeToOpenState() {
        assert tipScreen == null : "Tip screen should not be open";
        tipScreen = new RequestTipScreen(this);
        removeAll();
        ctaScreen = null;
        add(tipScreen);

        revalidate();
        repaint();
    }

    void changeToClosedState() {
        assert ctaScreen == null : "CTA screen should not be open";
        ctaScreen = new RequestTipCTAScreen(this);
        removeAll();
        tipScreen = null;
        add(ctaScreen);

        revalidate();
        repaint();
    }

    Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }

}
