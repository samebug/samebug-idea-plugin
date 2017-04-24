package com.samebug.clients.http.entities.jsonapi;

import org.jetbrains.annotations.NotNull;

public abstract class JsonResource<Data> {
    private Data data;

    @NotNull
    public Data getData() {
        return data;
    }
}
