package com.samebug.clients.http.entities2.solution;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class SearchableSolution extends SolutionSlot {
    private Date createdAt;
    private Document document;

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    @NotNull
    public Document getDocument() {
        return document;
    }
}
