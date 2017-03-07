package com.samebug.clients.common.ui.component.solutions;

public interface IRequestTip {
    void startRequestTip();
    void interruptRequestTip();
    void successRequestTip(/*TODO param*/);

    interface Listener {
        void askBugmates(IRequestTip source, String description);
    }
}
