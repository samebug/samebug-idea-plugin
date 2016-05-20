package com.samebug.clients.search.api.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.entities.StackTraceSearchGroup;
import com.samebug.clients.search.api.entities.TextSearchGroup;

public final class SearchGroupAdapter extends AbstractObjectAdapter<SearchGroup> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends SearchGroup>>builder()
                .put("stacktrace", StackTraceSearchGroup.class)
                .put("text", TextSearchGroup.class)
                .build();
    }
}
