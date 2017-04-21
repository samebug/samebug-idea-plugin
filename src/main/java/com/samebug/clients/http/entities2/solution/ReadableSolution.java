package com.samebug.clients.http.entities2.solution;

import com.samebug.clients.http.entities2.user.RegisteredSamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class ReadableSolution extends SolutionSlot {
    private Date createdAt;
    private Date modified;
    private String exceptionType;
    private String message;
    private String messageSlug;
    private RegisteredSamebugUser createdBy;
    private Integer workspaceId;
    private Integer marks;
    private Document document;

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

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

    @NotNull
    public Document getDocument() {
        return document;
    }
}
