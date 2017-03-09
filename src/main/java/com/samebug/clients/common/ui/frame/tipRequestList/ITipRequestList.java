package com.samebug.clients.common.ui.frame.tipRequestList;

import com.samebug.clients.common.ui.component.tipRequest.ITipRequestPreview;

import java.util.List;

public interface ITipRequestList {
    final class Model {
        public final List<ITipRequestPreview.Model> requestPreviews;

        public Model(Model rhs) {
            this(rhs.requestPreviews);
        }

        public Model(List<ITipRequestPreview.Model> requestPreviews) {
            this.requestPreviews = requestPreviews;
        }
    }

    interface Listener {
        void moreClicked();
    }

}
