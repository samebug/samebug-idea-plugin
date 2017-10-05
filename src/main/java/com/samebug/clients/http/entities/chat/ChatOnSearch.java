package com.samebug.clients.http.entities.chat;

import com.samebug.clients.http.entities.search.Search;
import org.jetbrains.annotations.NotNull;

public final class ChatOnSearch {
    private Search search;

    @NotNull
    public Search getSearch() {
        return search;
    }
}
