package com.samebug.clients.common.ui.component.util;

public interface IFrame {
    void showNetworkError();
    void hideNetworkError();
    void showAuthenticationError();
    void hideAuthenticationError();
    void popupError(String message);
}
