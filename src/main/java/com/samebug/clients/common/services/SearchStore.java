package com.samebug.clients.common.services;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.model.StackTraceSearchListener;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.tracking.SearchInfo;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SearchStore implements StackTraceSearchListener {
    private final Project myProject;
    private final Map<UUID, String> traces;
    private final Map<UUID, SearchResults> results;

    public SearchStore(Project project) {
        myProject = project;
        traces = new ConcurrentHashMap<UUID, String>();
        results = new ConcurrentHashMap<UUID, SearchResults>();
        MessageBusConnection projectConnection = project.getMessageBus().connect(project);
        projectConnection.subscribe(StackTraceSearchListener.TOPIC, this);
    }

    public Map<UUID, String> getTraces() {
        return traces;
    }

    @Nullable
    public SearchResults getResult(UUID requestId) {
        return results.get(requestId);
    }

    @Override
    public void searchStart(SearchInfo searchInfo, String stackTrace) {
        traces.put(searchInfo.getRequestId(), stackTrace);
    }

    @Override
    public void searchSucceeded(SearchInfo searchInfo, SearchResults result) {
        results.put(searchInfo.getRequestId(), result);
    }

    @Override
    public void timeout(SearchInfo searchInfo) {
        traces.remove(searchInfo.getRequestId());
    }

    @Override
    public void unauthorized(SearchInfo searchInfo) {
        traces.remove(searchInfo.getRequestId());
    }

    @Override
    public void searchFailed(SearchInfo searchInfo, SamebugClientException error) {
        traces.remove(searchInfo.getRequestId());
    }
}
