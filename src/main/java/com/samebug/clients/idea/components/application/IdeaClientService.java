package com.samebug.clients.idea.components.application;

import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.History;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;

/**
 * Created by poroszd on 2/23/16.
 */
public class IdeaClientService {
    private SamebugClient client = new SamebugClient(null);

    public IdeaClientService(final String apiKey) {
        this.client = new SamebugClient(apiKey);
    }

    public SearchResults searchSolutions(String stacktrace)
            throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
        return client.searchSolutions(stacktrace);
    }

    public UserInfo getUserInfo(String apiKey) throws UnknownApiKey, SamebugClientException {
        return client.getUserInfo(apiKey);
    }

    public History getSearchHistory() throws SamebugClientException {
        return client.getSearchHistory();
    }

    public void trace(TrackEvent event) throws SamebugClientException {
        client.trace(event);
    }
}