package com.samebug.clients.http.entities2.authentication;

import com.samebug.clients.http.entities2.user.RegisteredSamebugUser;
import org.jetbrains.annotations.NotNull;

public final class AuthenticationResponse {
    private RegisteredSamebugUser user;

    @NotNull
    public RegisteredSamebugUser getUser() {
        return user;
    }
}
