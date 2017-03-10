package com.samebug.clients.common.ui.frame.tipRequest;

import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;

public interface ITipRequestTabs {
    final class Model {
        public final IWebResultsTab.Model webResults;
        public final ITipRequestTab.Model tipRequest;
        public final IHelpOthersCTA.Model cta;

        public Model(Model rhs) {
            this(rhs.webResults, rhs.tipRequest, rhs.cta);
        }

        public Model(IWebResultsTab.Model webResults, ITipRequestTab.Model tipRequest, IHelpOthersCTA.Model cta) {
            this.webResults = webResults;
            this.tipRequest = tipRequest;
            this.cta = cta;
        }
    }
}
