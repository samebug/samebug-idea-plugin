package com.samebug.clients.http.entities2.mark;

import com.samebug.clients.http.entities2.user.SamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public abstract class Mark {
    private Integer id;
    private SamebugUser markedBy;
    private Integer workspaceId;
    private Date timestamp;
    private Date cancelTimestamp;
    private Integer solutionId;

    @NotNull
    public final Integer getId() {
        return id;
    }

    @NotNull
    public final SamebugUser getMarkedBy() {
        return markedBy;
    }

    @Nullable
    public final Integer getWorkspaceId() {
        return workspaceId;
    }

    @NotNull
    public final Date getTimestamp() {
        return timestamp;
    }

    @Nullable
    public final Date getCancelTimestamp() {
        return cancelTimestamp;
    }

    @NotNull
    public final Integer getSolutionId() {
        return solutionId;
    }
}
