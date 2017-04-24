package com.samebug.clients.http.entities.bugmate;

import com.samebug.clients.http.entities.search.SearchGroup;
import com.samebug.clients.http.entities.user.SamebugUser;
import org.jetbrains.annotations.NotNull;

public final class BugmateMatch {
    private SamebugUser bugmate;
    private SearchGroup matchingGroup;
    private Integer level;

    @NotNull
    public SamebugUser getBugmate() {
        return bugmate;
    }

    @NotNull
    public SearchGroup getMatchingGroup() {
        return matchingGroup;
    }

    @NotNull
    public Integer getLevel() {
        return level;
    }
}
