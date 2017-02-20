package com.samebug.clients.common.ui.component.solutions;

import com.intellij.util.messages.Topic;

public interface IResultTabs {
    final class Model {
        public final IWebResultsTab.Model webResults;
        public final ITipResultsTab.Model tipResults;
        public final IHelpOthersCTA.Model cta;

        public Model(Model rhs) {
            this(rhs.webResults, rhs.tipResults, rhs.cta);
        }

        public Model(IWebResultsTab.Model webResults, ITipResultsTab.Model tipResults, IHelpOthersCTA.Model cta) {
            this.webResults = webResults;
            this.tipResults = tipResults;
            this.cta = cta;
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("IResultTabs", Listener.class);
    }
}
