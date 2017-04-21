package com.samebug.clients.http.entities2.solution;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SamebugTip extends Document {
    private String message;
    private ExternalDocument referencedDocument;

    @NotNull
    public String getMessage() {
        return message;
    }

    @Nullable
    public ExternalDocument getReferencedDocument() {
        return referencedDocument;
    }
}
