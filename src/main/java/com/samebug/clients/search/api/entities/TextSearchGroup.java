package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;

public final class TextSearchGroup extends SearchGroup {
    @NotNull
    public TextSearch lastSearch;

    @Override
    @NotNull
    public TextSearch getLastSearch() {
        return lastSearch;
    }
}
