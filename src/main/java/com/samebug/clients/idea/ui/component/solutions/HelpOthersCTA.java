package com.samebug.clients.idea.ui.component.solutions;

import com.samebug.clients.idea.ui.component.util.panel.EmphasizedPanel;

public class HelpOthersCTA extends EmphasizedPanel {

    public final static class Model {
        protected final int usersWaitingHelp;

        public Model(Model rhs) {
            this(rhs.usersWaitingHelp);
        }

        public Model(int usersWaitingHelp) {
            this.usersWaitingHelp = usersWaitingHelp;
        }
    }
}
