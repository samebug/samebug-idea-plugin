package com.samebug.clients.common.api.entities.helpRequest;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class IncomingTip {
    @NotNull
    public final Requester author;
    @NotNull
    public final String message;
    @NotNull
    public final Date createdAt;

    public IncomingTip(@NotNull Requester author, @NotNull String message, @NotNull Date createdAt) {
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }
}
