package com.samebug.clients.swing.ui.component.util.errorBarPane;

import com.samebug.clients.swing.ui.SamebugBundle;

public class BasicFrame extends ErrorBarPane {
    protected final ErrorBar networkErrorBar;
    protected final ErrorBar authenticationErrorBar;

    public BasicFrame() {
        networkErrorBar = new ErrorBar(SamebugBundle.message("samebug.component.errorBar.network"));
        authenticationErrorBar = new ErrorBar(SamebugBundle.message("samebug.component.errorBar.authentication"));
    }

    public void showNetworkError() {
        addErrorBar(networkErrorBar);
    }
    public void hideNetworkError() {
        removeErrorBar(networkErrorBar);
    }
    public void showAuthenticationError() {
        addErrorBar(authenticationErrorBar);
    }
    public void hideAuthenticationError() {
        removeErrorBar(authenticationErrorBar);
    }
    public void popupError(String message) {
        popupErrorBar(new ErrorBar(message));
    }
}
