package com.samebug.clients.http.entities2.solution;

import com.samebug.clients.http.entities2.user.User;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Date;

public abstract class Document {
    private URL url;
    private User author;
    private Date createdAt;

    @NotNull
    public final URL getUrl() {
        return url;
    }

    @NotNull
    public final User getAuthor() {
        return author;
    }

    @NotNull
    public final Date getCreatedAt() {
        return createdAt;
    }
}
