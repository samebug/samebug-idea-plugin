package com.samebug.clients.http.entities.solution;

import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class ReadableSolution<D extends Document> extends SolutionSlot<D> {
    private Date modified;
    private String exceptionType;
    private String message;
    private String messageSlug;
    private RegisteredSamebugUser createdBy;
    private Integer workspaceId;
    private Integer marks;

    @NotNull
    public Date getModified() {
        return modified;
    }

    @NotNull
    public String getExceptionType() {
        return exceptionType;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public String getMessageSlug() {
        return messageSlug;
    }

    @NotNull
    public RegisteredSamebugUser getCreatedBy() {
        return createdBy;
    }

    @Nullable
    public Integer getWorkspaceId() {
        return workspaceId;
    }

    @NotNull
    public Integer getMarks() {
        return marks;
    }
}
