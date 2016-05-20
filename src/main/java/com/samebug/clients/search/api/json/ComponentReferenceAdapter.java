package com.samebug.clients.search.api.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.search.api.entities.*;

public final class ComponentReferenceAdapter extends AbstractObjectAdapter<ComponentReference> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends ComponentReference>>builder()
                .put("app", ApplicationComponentReference.class)
                .put("default", DefaultComponentReference.class)
                .put("library", LibraryComponentReference.class)
                .put("vendor", VendorComponentReference.class)
                .build();
    }
}
