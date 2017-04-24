package com.samebug.clients.http.entities.solution;

import com.samebug.clients.http.entities.missing.Source;
import com.samebug.clients.http.entities.user.User;
import org.jetbrains.annotations.NotNull;

public final class ExternalDocument extends Document {
    private User author;
    private String documentType;
    private String title;
    private Source source;

    @NotNull
    public final User getAuthor() {
        return author;
    }

    @NotNull
    public String getDocumentType() {
        return documentType;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public Source getSource() {
        return source;
    }
}
