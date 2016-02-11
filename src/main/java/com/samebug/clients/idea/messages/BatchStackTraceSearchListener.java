package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;
import com.samebug.clients.search.api.entities.SearchResults;

import java.util.List;

/**
 * Created by poroszd on 2/11/16.
 */
public interface BatchStackTraceSearchListener {
    Topic<BatchStackTraceSearchListener> BATCH_SEARCH_TOPIC = Topic.create("batch stacktrace search", BatchStackTraceSearchListener.class);

    public void batchStart();

    // TODO later we might want more details about the failed ones, currently it's just the number of failed searches.
    public void batchFinished(List<SearchResults> results, int failed);
}
