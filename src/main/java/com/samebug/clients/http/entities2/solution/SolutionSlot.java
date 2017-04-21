package com.samebug.clients.http.entities2.solution;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public abstract class SolutionSlot<D extends Document> {
    private Integer id;
    private Date createdAt;
    private D document;

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    @NotNull
    public D getDocument() {
        return document;
    }

    @NotNull
    public final Integer getId() {
        return id;
    }
}
