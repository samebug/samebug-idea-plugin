package com.samebug.clients.http.entities.solution;

import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SamebugTip extends Document {
    private RegisteredSamebugUser author;
    private String message;
    private ExternalDocument referencedDocument;

    @NotNull
    public final RegisteredSamebugUser getAuthor() {
        return author;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @Nullable
    public ExternalDocument getReferencedDocument() {
        return referencedDocument;
    }
}
