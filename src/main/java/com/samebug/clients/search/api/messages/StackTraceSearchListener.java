package com.samebug.clients.search.api.messages;

import com.intellij.util.messages.Topic;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

/**
 * Created by poroszd on 2/5/16.
 */
public interface StackTraceSearchListener {
    Topic<StackTraceSearchListener> SEARCH_TOPIC = Topic.create("stacktrace search", StackTraceSearchListener.class);

    void searchStart(String id, String stackTrace);

    void searchSucceeded(String id, SearchResults results);

    void timeout(String id);

    void unauthorized(String id);

    void searchFailed(String id, SamebugClientException error);
}
