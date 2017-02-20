package com.samebug.clients.common.ui.component.solutions;

import com.intellij.util.messages.Topic;

import java.util.List;

public interface IWebResultsTab {
    final class Model {
        public final List<IWebHit.Model> webHits;

        public Model(Model rhs) {
            this(rhs.webHits);
        }

        public Model(List<IWebHit.Model> webHits) {
            this.webHits = webHits;
        }

        public int getHitsSize() {
            return webHits.size();
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("IWebResultsTab", Listener.class);

        void moreClicked();
    }
}
