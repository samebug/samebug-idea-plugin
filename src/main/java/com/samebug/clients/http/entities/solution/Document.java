package com.samebug.clients.http.entities.solution;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Date;

public abstract class Document {
    private URL url;
    private Date createdAt;

    @NotNull
    public final URL getUrl() {
        return url;
    }

    @NotNull
    public final Date getCreatedAt() {
        return createdAt;
    }
}
