package com.samebug.clients.http.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.http.entities.search.NonAccessibleSearch;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.search.StackTraceSearch;
import com.samebug.clients.http.entities.search.TextSearch;

public class SearchAdapter extends AbstractObjectAdapter<Search> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends Search>>builder()
                .put("stacktrace-search", StackTraceSearch.class)
                .put("text-search", TextSearch.class)
                .put("nonaccessible-search", NonAccessibleSearch.class)
                .build();
    }
}
