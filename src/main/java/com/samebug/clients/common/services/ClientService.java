package com.samebug.clients.common.services;

import com.samebug.clients.http.client.SamebugClient;
import org.jetbrains.annotations.NotNull;

public interface ClientService {
    @NotNull
    SamebugClient getClient();
}
