package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StackTraceWithBreadCrumbs {
    @NotNull
    public Integer _id;
    // TODO trace
    @NotNull
    public List<BreadCrumb> breadCrumbs;
}
