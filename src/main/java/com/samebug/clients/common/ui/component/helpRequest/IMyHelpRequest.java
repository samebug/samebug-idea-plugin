package com.samebug.clients.common.ui.component.helpRequest;

import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;

public interface IMyHelpRequest {
    void startRevoke();

    void failRevoke();

    void successRevoke();

    final class Model {
        // TODO this is a rest api entity in the ui model, remove it
        public final MyHelpRequest helpRequest;

        public Model(Model rhs) {
            this(rhs.helpRequest);
        }

        public Model(MyHelpRequest helpRequest) {
            this.helpRequest = helpRequest;
        }
    }

    interface Listener {
        // TODO the UI should not know the help request id, it should come from the controller
        void revokeHelpRequest(IMyHelpRequest source, String helpRequestId);
    }
}
