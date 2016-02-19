package com.samebug.clients.search.api.entities.tracking;

import java.util.UUID;

/**
 * Created by poroszd on 2/19/16.
 */
public class SearchInfo {
    private final DebugSessionInfo sessionInfo;
    private final UUID requestId = UUID.randomUUID();

    public SearchInfo(DebugSessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public DebugSessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public UUID getRequestId() {
        return requestId;
    }
}
