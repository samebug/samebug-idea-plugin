package com.samebug.clients.http.entities2.search;

import org.jetbrains.annotations.NotNull;

public final class StackTraceSearchHit extends SearchHit {
    private Integer level;

    @NotNull
    public Integer getLevel() {
        return level;
    }
}
