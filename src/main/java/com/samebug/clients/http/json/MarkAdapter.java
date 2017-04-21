package com.samebug.clients.http.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.http.entities2.mark.*;

public class MarkAdapter extends AbstractObjectAdapter<Mark> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends Mark>>builder()
                .put("mark-crashdoc", CrashDochMark.class)
                .put("mark-solution", SolutionViewMark.class)
                .put("mark-textsearch", TextSearchMark.class)
                .put("mark-stacktracesearch", StackTraceSearchMark.class)
                .build();
    }
}
