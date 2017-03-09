package com.samebug.clients.common.ui.frame.tipRequest;

import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.component.tipRequest.ITipRequest;

import java.util.List;

public interface ITipRequestTab {
    final class Model {
        public final ITipRequest.Model tipRequest;
        public final List<ITipHit.Model> tipHits;

        public Model(Model rhs) {
            this(rhs.tipHits, rhs.tipRequest);
        }

        public Model(List<ITipHit.Model> tipHits, ITipRequest.Model tipRequest) {
            this.tipHits = tipHits;
            this.tipRequest = tipRequest;
        }
    }

    interface Listener {
        void sendTip(String tipBody);
        void clickExplanation();
    }
}
