package com.samebug.clients.http.entities2.jsonapi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JsonResource<Data, Meta> {
    @NotNull
    public final Data data;
    @Nullable
    public final Meta meta;

    public JsonResource(@NotNull Data data, @Nullable Meta meta) {
        this.data = data;
        this.meta = meta;
    }
}
