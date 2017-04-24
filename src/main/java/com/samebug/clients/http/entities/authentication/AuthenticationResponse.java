package com.samebug.clients.http.entities.authentication;

import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import org.jetbrains.annotations.NotNull;

public final class AuthenticationResponse {
    private RegisteredSamebugUser user;

    @NotNull
    public RegisteredSamebugUser getUser() {
        return user;
    }
}
