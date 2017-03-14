package com.samebug.clients.common.api.entities.search;

import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import org.jetbrains.annotations.Nullable;

public final class Group {
    @Nullable
    public final MyHelpRequest helpRequest;

    public Group(@Nullable MyHelpRequest helpRequest) {
        this.helpRequest = helpRequest;
    }
}
