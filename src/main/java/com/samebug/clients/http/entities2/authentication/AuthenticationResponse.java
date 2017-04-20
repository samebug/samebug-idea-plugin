package com.samebug.clients.http.entities2.authentication;

import com.samebug.clients.http.entities2.user.RegisteredSamebugUser;

public final class AuthenticationResponse {
    private final RegisteredSamebugUser user;

    public AuthenticationResponse(RegisteredSamebugUser user) {
        this.user = user;
    }

    public RegisteredSamebugUser getUser() {
        return user;
    }
}
