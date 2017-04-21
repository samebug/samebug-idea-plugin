package com.samebug.clients.http.entities2.jsonapi;

import org.jetbrains.annotations.NotNull;

public class JsonResourceWithMeta<Data, Meta> extends JsonResource<Data> {
    private Meta meta;

    @NotNull
    public Meta getMeta() {
        return meta;
    }
}
