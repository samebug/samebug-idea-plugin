package com.samebug.clients.swing.ui.component.solutions.requestTip;

import com.samebug.clients.common.ui.component.solutions.IBugmateList;
import com.samebug.clients.swing.ui.global.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

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

    }

    public void interruptRequestTip() {

    }

    public void successRequestTip() {

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
