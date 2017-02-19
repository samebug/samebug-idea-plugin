package com.samebug.clients.common.ui.component.solutions;

import com.intellij.util.messages.Topic;

public interface IExceptionHeaderPanel {
    final class Model {
        public final String title;

        public Model(Model rhs) {
            this(rhs.title);
        }

        public Model(String title) {
            this.title = title;
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("ExceptionHeaderPanel", Listener.class);

        void titleClicked();
    }
}
