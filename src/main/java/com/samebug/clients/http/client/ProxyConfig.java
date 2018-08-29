/*
 * Copyright 2018 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
