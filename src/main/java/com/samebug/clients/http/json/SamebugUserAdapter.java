package com.samebug.clients.http.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import com.samebug.clients.http.entities.user.SamebugUser;
import com.samebug.clients.http.entities.user.SamebugVisitor;

public class SamebugUserAdapter extends AbstractObjectAdapter<SamebugUser> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends SamebugUser>>builder()
                .put("samebug-user", RegisteredSamebugUser.class)
                .put("samebug-visitor", SamebugVisitor.class)
                .build();
    }
}
