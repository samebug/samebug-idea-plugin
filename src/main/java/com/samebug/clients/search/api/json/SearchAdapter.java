package com.samebug.clients.search.api.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.search.api.entities.Search;
import com.samebug.clients.search.api.entities.StackTraceSearch;
import com.samebug.clients.search.api.entities.TextSearch;

final class SearchAdapter extends AbstractObjectAdapter<Search> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends Search>>builder()
                .put("stacktrace", StackTraceSearch.class)
                .put("text", TextSearch.class)
                .build();
    }
}
