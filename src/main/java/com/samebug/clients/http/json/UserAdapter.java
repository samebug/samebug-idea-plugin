package com.samebug.clients.http.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.http.entities.user.ExternalUser;
import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import com.samebug.clients.http.entities.user.SamebugVisitor;
import com.samebug.clients.http.entities.user.User;

public class UserAdapter extends AbstractObjectAdapter<User> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends User>>builder()
                .put("samebug-user", RegisteredSamebugUser.class)
                .put("samebug-visitor", SamebugVisitor.class)
                .put("external-user", ExternalUser.class)
                .build();
    }
}
