package com.samebug.clients.http.entities.chat;

import com.samebug.clients.http.entities.user.SamebugUser;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class ChatRoom {
    private String id;
    private SamebugUser createdBy;
    private Date created;
    private Date updated;
    private String title;
    private ChatOnSearch source;

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public SamebugUser getCreatedBy() {
        return createdBy;
    }

    @NotNull
    public Date getCreated() {
        return created;
    }

    @NotNull
    public Date getUpdated() {
        return updated;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public ChatOnSearch getSource() {
        return source;
    }
}
