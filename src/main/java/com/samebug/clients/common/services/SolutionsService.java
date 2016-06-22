package com.samebug.clients.common.services;

import com.intellij.util.containers.HashMap;
import com.samebug.clients.search.api.entities.RestHit;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.entities.Solutions;
import com.samebug.clients.search.api.entities.StackTraceSearchGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SolutionsService {
    @NotNull
    final Map<Integer, Solutions> searches;

    public SolutionsService() {
        searches = new HashMap<Integer, Solutions>();
    }

    public void setSolutions(@NotNull final int searchId, @NotNull final Solutions solutions) {
        searches.put(searchId, solutions);
    }

    @Nullable
    public Solutions getSolutions(@NotNull final int searchId) {
        return searches.get(searchId);
    }

    public boolean canBeMarked(final int userId, @NotNull final SearchGroup searchGroup, @NotNull final RestHit hit) {
        return hit.createdBy == null
                || !hit.createdBy.id.equals(userId)
                || !(searchGroup instanceof StackTraceSearchGroup)
                || !hit.stackTraceId.equals(((StackTraceSearchGroup) searchGroup).lastSearch.stackTrace.stackTraceId);
    }
}
