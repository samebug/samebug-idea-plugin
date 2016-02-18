package com.samebug.clients.idea.tracking;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.search.api.entities.TrackEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by poroszd on 2/18/16.
 */
public class Events {
    public static TrackEvent projectOpen(Project project) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("projectName", project.getName());
        return createEvent(fields);
    }

    public static TrackEvent pluginInstall() {
        Map<String, String> fields = new HashMap<String, String>();
        return createEvent(fields);
    }

    private static TrackEvent createEvent(Map<String, String> fields) {
        addCommonFields(fields);
        return new TrackEvent(fields);
    }

    private static void addCommonFields(Map<String, String> fields) {
        try {
            Integer userId = IdeaSamebugPlugin.getInstance().getState().getUserId();
            if (userId != null) fields.put("userId", userId.toString());
        } catch (Throwable e) {
            LOGGER.debug("failed to write userId to tracking event", e);
        }
        try {
            String instanceId = IdeaSamebugPlugin.getInstance().getState().getInstanceId();
            if (instanceId != null) fields.put("instanceId", instanceId);
        } catch (Throwable e) {
            LOGGER.debug("failed to write instanceId to tracking event", e);
        }
    }

    private final static Logger LOGGER = Logger.getInstance(Events.class);

}
