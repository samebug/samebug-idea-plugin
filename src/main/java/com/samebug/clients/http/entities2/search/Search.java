package com.samebug.clients.http.entities2.search;

import org.jetbrains.annotations.NotNull;

public abstract class Search {
    private Integer id;

    @NotNull
    public final Integer getId() {
        return id;
    }
}
