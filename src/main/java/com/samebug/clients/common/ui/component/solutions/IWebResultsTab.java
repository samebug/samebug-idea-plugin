package com.samebug.clients.common.ui.component.solutions;

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
}
