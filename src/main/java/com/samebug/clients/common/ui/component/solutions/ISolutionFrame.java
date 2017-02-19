package com.samebug.clients.common.ui.component.solutions;

import com.samebug.clients.common.ui.component.profile.IProfilePanel;

public interface ISolutionFrame {
    void setContent(Model model);
    void setLoading();
    void setGenericError();
    void setRetriableError();
    void setServerError();
    void setAuthenticationError();
    void setAuthorizationError();



    final class Model {
        public final IExceptionHeaderPanel.Model header;
        public final IResultTabs.Model resultTabs;
        public final IProfilePanel.Model profilePanel;

        public Model(Model rhs) {
            this(rhs.resultTabs, rhs.header, rhs.profilePanel);
        }

        public Model(IResultTabs.Model resultTabs, IExceptionHeaderPanel.Model header, IProfilePanel.Model profilePanel) {
            this.resultTabs = resultTabs;
            this.header = header;
            this.profilePanel = profilePanel;
        }
    }
}
