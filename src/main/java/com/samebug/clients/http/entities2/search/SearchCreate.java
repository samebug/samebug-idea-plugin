package com.samebug.clients.http.entities2.search;

import org.jetbrains.annotations.NotNull;

public final class SearchCreate {
    public final String type;
    @NotNull
    private final String stacktrace;

    public SearchCreate(@NotNull String stacktrace) {
        this.stacktrace = stacktrace;
        type = "stacktrace-search";
    }

    @NotNull
    public String getStacktrace() {
        return stacktrace;
    }
}
