package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextSearch extends Search {
    @NotNull
    public String query;
    @Nullable
    public String errorType;
}
