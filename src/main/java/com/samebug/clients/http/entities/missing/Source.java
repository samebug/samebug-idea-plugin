package com.samebug.clients.http.entities.missing;

import org.jetbrains.annotations.NotNull;

public final class Source {
    private String name;
    private String icon;

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getIcon() {
        return icon;
    }
}
