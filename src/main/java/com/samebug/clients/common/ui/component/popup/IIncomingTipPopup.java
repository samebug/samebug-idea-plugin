package com.samebug.clients.common.ui.component.popup;

import java.net.URL;

public interface IIncomingTipPopup {
    final class Model {
        public final String tipBody;
        public final String displayName;
        public final URL avatarUrl;

        public Model(Model rhs) {
            this(rhs.tipBody, rhs.displayName, rhs.avatarUrl);
        }

        public Model(String tipBody, String displayName, URL avatarUrl) {
            this.tipBody = tipBody;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
        }
    }
}
