package com.samebug.clients.swing.ui.testModules;

import com.samebug.clients.common.api.entities.tracking.TrackEvent;
import com.samebug.clients.swing.ui.modules.TrackingService;

public final class TestTrackingService extends TrackingService {
    @Override
    protected void internalTrace(TrackEvent event) {
        System.out.println(event);
    }
}
