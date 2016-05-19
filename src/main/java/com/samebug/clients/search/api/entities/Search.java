package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public abstract class Search {
    @NotNull
    public Integer _id;
    @NotNull
    public Date timestamp;
    @Nullable
    public String visitorId;
    @Nullable
    public Long userId;
    @Nullable
    public Long teamId;
}
