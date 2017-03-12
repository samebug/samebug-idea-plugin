package com.samebug.clients.common.api.entities.helpRequest;

import java.util.List;

public final class IncomingHelpRequests {
    public final List<MatchingHelpRequest> helpRequests;

    public IncomingHelpRequests(List<MatchingHelpRequest> helpRequests) {
        this.helpRequests = helpRequests;
    }
}
