package com.samebug.clients.search.api.entities.tracking;

import java.util.UUID;

/**
 * Created by poroszd on 2/19/16.
 */
public class DebugSessionInfo {
    private final UUID id = UUID.randomUUID();
    private final String sessionType;

    public DebugSessionInfo(String sessionType) {
        this.sessionType = sessionType;
    }

    public UUID getId() {
        return id;
    }
    public String getSessionType() {
        return sessionType;
    }
}
