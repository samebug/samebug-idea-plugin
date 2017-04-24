package com.samebug.clients.http.entities.helprequest;

import com.samebug.clients.http.entities.search.SearchGroup;
import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class HelpRequest {
    private String id;
    private RegisteredSamebugUser requester;
    private Long workspaceId;
    private Integer searchId;
    private SearchGroup searchGroup;
    private String context;
    private Date createdAt;
    private Date revokedAt;

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public RegisteredSamebugUser getRequester() {
        return requester;
    }

    @Nullable
    public Long getWorkspaceId() {
        return workspaceId;
    }

    @NotNull
    public Integer getSearchId() {
        return searchId;
    }

    @NotNull
    public SearchGroup getSearchGroup() {
        return searchGroup;
    }

    @Nullable
    public String getContext() {
        return context;
    }

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    @Nullable
    public Date getRevokedAt() {
        return revokedAt;
    }
}
