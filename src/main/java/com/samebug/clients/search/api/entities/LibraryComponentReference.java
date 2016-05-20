package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LibraryComponentReference extends ComponentReference {
    @NotNull
    public Integer id;
    @Nullable
    public String mavenId;
    @NotNull
    public String slug;
    @Nullable
    public String description;
}
