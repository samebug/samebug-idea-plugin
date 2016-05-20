package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StackTraceWithBreadCrumbs {
    @NotNull
    public Integer stackTraceId;
    @NotNull
    public Exception trace;
    @NotNull
    public List<BreadCrumb> breadCrumbs;
}
