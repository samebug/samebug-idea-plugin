package com.samebug.clients.http.entities2.jsonapi;

import org.jetbrains.annotations.NotNull;

public abstract class JsonResource<Data> {
    private Data data;

    @NotNull
    public Data getData() {
        return data;
    }
}
