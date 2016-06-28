package com.samebug.clients.common.services;

import com.samebug.clients.search.api.entities.RestHit;
import org.jetbrains.annotations.NotNull;

final public class RestHits {
    @NotNull
    public static RestHit asMarked(@NotNull final RestHit hit) {
        RestHit marked = new RestHit(hit);
        marked.score++;
        marked.markId = -1;
        return marked;
    }
    @NotNull
    public static RestHit asUnmarked(@NotNull final RestHit hit) {
        RestHit marked = new RestHit(hit);
        marked.score--;
        marked.markId = null;
        return marked;
    }
}
