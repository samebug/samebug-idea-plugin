package com.samebug.clients.http.entities2.solution;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class NonSearchableSolution extends SolutionSlot {
    private Date createdAt;
    private Document document;
    private Boolean deleted;
    private Boolean invalid;

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    @NotNull
    public Document getDocument() {
        return document;
    }

    @NotNull
    public Boolean getDeleted() {
        return deleted;
    }

    @NotNull
    public Boolean getInvalid() {
        return invalid;
    }
}
