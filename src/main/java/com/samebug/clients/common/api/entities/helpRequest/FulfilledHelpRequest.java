package com.samebug.clients.common.api.entities.helpRequest;

import org.jetbrains.annotations.NotNull;

public class FulfilledHelpRequest {
    @NotNull
    public final MyHelpRequest helpRequest;
    @NotNull
    public final IncomingTip tip;

    public FulfilledHelpRequest(@NotNull MyHelpRequest helpRequest, @NotNull IncomingTip tip) {
        this.helpRequest = helpRequest;
        this.tip = tip;
    }
}
