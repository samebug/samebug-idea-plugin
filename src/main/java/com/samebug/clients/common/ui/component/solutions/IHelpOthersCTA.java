package com.samebug.clients.common.ui.component.solutions;

import com.intellij.util.messages.Topic;

public interface IHelpOthersCTA {
    final class Model {
        public final int usersWaitingHelp;

        public Model(Model rhs) {
            this(rhs.usersWaitingHelp);
        }

        public Model(int usersWaitingHelp) {
            this.usersWaitingHelp = usersWaitingHelp;
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("IHelpOthersCTA", Listener.class);

        void ctaClicked(IHelpOthersCTA source);
    }
}
