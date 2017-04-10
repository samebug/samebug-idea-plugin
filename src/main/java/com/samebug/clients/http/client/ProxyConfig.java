package com.samebug.clients.http.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProxyConfig {
    @Nullable
    public final String host;
    public final int port;
    @Nullable
    public final String login;
    @Nullable
    public final String password;

    public ProxyConfig(@Nullable String host, int port, @Nullable String login, @Nullable String password) {
        this.host = host;
        this.port = port;
        this.login = login;
        this.password = password;
    }

    public ProxyConfig(@NotNull final ProxyConfig rhs) {
        this.host = rhs.host;
        this.port = rhs.port;
        this.login = rhs.login;
        this.password = rhs.password;
    }
}
