package com.samebug.clients.idea.tracking;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.search.api.entities.TrackEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by poroszd on 2/18/16.
 */
public class Events {
    public static TrackEvent pluginInstall() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "plugin-install");
        return createEvent(fields, null);
    }

    public static TrackEvent apiKeySet() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "apikey-set");
        return createEvent(fields, null);
    }

    public static TrackEvent projectOpen(Project project) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "project-open");
        return createEvent(fields, project);
    }

    public static TrackEvent projectClose(Project project) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "project-close");
        return createEvent(fields, project);
    }

    public static TrackEvent debugStart(Project project) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "debug-start");
        return createEvent(fields, project);
    }

    public static TrackEvent debugStop(Project project) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "debug-stop");
        return createEvent(fields, project);
    }

    public static TrackEvent toolWindowOpen(Project project, String from) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "toolWindow-open");
        fields.put("from", from);
        return createEvent(fields, project);
    }

    public static TrackEvent searchResultClick(Project project, String searchId) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "searchResult-click");
        fields.put("searchId", searchId);
        return createEvent(fields, project);
    }


    private static TrackEvent createEvent(Map<String, String> fields, @Nullable Project project) {
        addCommonFields(fields, project);
        return new TrackEvent(fields);
    }

    private static void addCommonFields(Map<String, String> fields, @Nullable Project project) {
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
        if (project != null) {
            fields.put("projectName", project.getName());
        }

    }

    private final static Logger LOGGER = Logger.getInstance(Events.class);

}
