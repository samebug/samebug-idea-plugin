package com.samebug.clients.http.form;

import org.jetbrains.annotations.NotNull;

public final class Error<Code extends Enum<Code>> {
    @NotNull
    public final Code code;

    public Error(@NotNull Code code) {
        this.code = code;
    }
}
