package com.samebug.clients.http.entities;

import com.samebug.clients.common.entities.search.ReadableSearchGroup;
import com.samebug.clients.http.entities.helprequest.HelpRequestMatch;
import com.samebug.clients.http.entities.search.SearchGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntityUtils {
    @Nullable
    public static ReadableSearchGroup getReadableStackTraceSearchGroup(@NotNull HelpRequestMatch match) {
        SearchGroup requesterGroup = match.getHelpRequest().getSearchGroup();
        SearchGroup mySearchGroup = match.getMatchingGroup();

        if (requesterGroup.getLastSearchId() != null && requesterGroup.getLastSearchInfo() != null) {
            return new ReadableSearchGroup(requesterGroup.getLastSearchInfo(), requesterGroup.getLastSearchId());
        } else if (mySearchGroup.getLastSearchId() != null && mySearchGroup.getLastSearchInfo() != null) {
            return new ReadableSearchGroup(mySearchGroup.getLastSearchInfo(), mySearchGroup.getLastSearchId());
        } else {
            return null;
        }
    }

    private EntityUtils() {}
}
