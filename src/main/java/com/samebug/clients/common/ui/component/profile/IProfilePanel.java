package com.samebug.clients.common.ui.component.profile;

import java.net.URL;

public interface IProfilePanel {
    final class Model {
        public final int messages;
        public final int marks;
        public final int tips;
        public final int thanks;
        public final String name;
        public final URL avatarUrl;

        public Model(Model rhs) {
            this(rhs.messages, rhs.marks, rhs.tips, rhs.thanks, rhs.name, rhs.avatarUrl);
        }

        public Model(int messages, int marks, int tips, int thanks, String name, URL avatarUrl) {
            this.messages = messages;
            this.marks = marks;
            this.tips = tips;
            this.thanks = thanks;
            this.name = name;
            this.avatarUrl = avatarUrl;
        }
    }
}
