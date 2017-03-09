package com.samebug.clients.common.ui.frame.tipRequest;

import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;

public interface ITipRequestTabs {
    final class Model {
        public final IWebResultsTab.Model webResults;
        public final ITipRequestTab.Model tipRequest;

        public Model(Model rhs) {
            this(rhs.webResults, rhs.tipRequest);
        }

        public Model(IWebResultsTab.Model webResults, ITipRequestTab.Model tipRequest) {
            this.webResults = webResults;
            this.tipRequest = tipRequest;
        }
    }
}
