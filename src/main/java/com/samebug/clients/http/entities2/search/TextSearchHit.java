package com.samebug.clients.http.entities2.search;

import org.jetbrains.annotations.NotNull;

public final class TextSearchHit extends SearchHit {
    private Double score;

    @NotNull
    public Double getScore() {
        return score;
    }
}
