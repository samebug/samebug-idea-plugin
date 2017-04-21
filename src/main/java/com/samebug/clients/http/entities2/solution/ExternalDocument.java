package com.samebug.clients.http.entities2.solution;

import com.samebug.clients.http.entities2.missing.Source;
import org.jetbrains.annotations.NotNull;

public final class ExternalDocument extends Document {
    private String documentType;
    private String title;
    private Source source;

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
