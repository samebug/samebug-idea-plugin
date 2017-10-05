package com.samebug.clients.common.ui.component.community;

public interface IAskForHelpViaChat {
    void startChat();

    void failStartChat(BadRequest errors);

    void successStartChat();

    final class Model {

        public Model(Model rhs) {
            this();
        }

        public Model() {
        }
    }

    final class BadRequest {
        public BadRequest() {
        }
    }

    interface Listener {
        void askTeammates(IAskForHelpViaChat source);
    }
}
