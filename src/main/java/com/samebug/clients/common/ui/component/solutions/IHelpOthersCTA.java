package com.samebug.clients.common.ui.component.solutions;

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
}
