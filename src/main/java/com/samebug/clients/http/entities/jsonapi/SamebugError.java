package com.samebug.clients.http.entities.jsonapi;

import org.jetbrains.annotations.NotNull;

public final class SamebugError<Code extends Enum<Code>> {
    @NotNull
    public final Code code;

    public SamebugError(@NotNull Code code) {
        this.code = code;
    }
}
