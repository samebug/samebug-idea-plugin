package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;

public final class VendorComponentReference extends ComponentReference {
    @NotNull
    public String packageName;

    @Override
    @NotNull
    public String getName() {
        return packageName;
    }

    @Override
    @NotNull
    public Integer getColor() {
        return 0;
    }
}
