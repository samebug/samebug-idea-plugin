package com.samebug.clients.common.ui.frame.tipRequestList;

public interface ITipRequestListHeader {
    final class Model {
        public final int numberOfTipRequests;

        public Model(Model rhs) {
            this(rhs.numberOfTipRequests);
        }

        public Model(int numberOfTipRequests) {
            this.numberOfTipRequests = numberOfTipRequests;
        }
    }
}
