package com.samebug.clients.common.ui.component.popup;

import java.net.URL;

public interface ITipRequestPopup {
    final class Model {
        public final String tipRequestBody;
        public final String displayName;
        public final URL avatarUrl;

        public Model(Model rhs) {
            this(rhs.tipRequestBody, rhs.displayName, rhs.avatarUrl);
        }

        public Model(String tipRequestBody, String displayName, URL avatarUrl) {
            this.tipRequestBody = tipRequestBody;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
        }
    }

    interface Listener {
        void answerClick();
        void laterClick();
    }
}
