package com.samebug.clients.http.entities2.search;

import com.samebug.clients.http.entities2.user.SamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class TextSearch extends Search {
    private SearchGroup group;
    private Date timestamp;
    private String query;
    private SamebugUser user;

    @NotNull
    public SearchGroup getGroup() {
        return group;
    }

    @NotNull
    public Date getTimestamp() {
        return timestamp;
    }

    @Nullable
    public String getQuery() {
        return query;
    }

    @NotNull
    public SamebugUser getUser() {
        return user;
    }
}
