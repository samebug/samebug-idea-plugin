package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;

public final class DefaultComponentReference extends ComponentReference {
    @Override
    @NotNull
    public String getName() {
        return "Default package";
    }

    @Override
    @NotNull
    public Integer getColor() {
        return 0;
    }
}
