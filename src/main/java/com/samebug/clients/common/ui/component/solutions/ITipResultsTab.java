package com.samebug.clients.common.ui.component.solutions;

import java.util.List;

public interface ITipResultsTab {
    final class Model {
        public final List<ITipHit.Model> tipHits;
        public final IBugmateList.Model bugmateList;

        public Model(Model rhs) {
            this(rhs.tipHits, rhs.bugmateList);
        }

        public Model(List<ITipHit.Model> tipHits, IBugmateList.Model bugmateList) {
            this.tipHits = tipHits;
            this.bugmateList = bugmateList;
        }

        public int getTipsSize() {
            return tipHits.size();
        }
    }
}
