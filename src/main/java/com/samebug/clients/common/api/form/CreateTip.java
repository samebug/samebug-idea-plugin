package com.samebug.clients.common.api.form;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CreateTip {
    public static final String BODY = "tip";
    public static final String E_TOO_SHORT = "MESSAGE_TOO_SHORT";
    public static final String E_TOO_LONG = "MESSAGE_TOO_LONG";
    public static final String E_NOT_YOUR_SEARCH = "NOT_YOUR_SEARCH";
    public static final String E_NOT_EXCEPTION_SEARCH = "NOT_EXCEPTION_SEARCH";
    public static final String E_UNKNOWN_SEARCH = "UNKNOWN_SEARCH";
    public static final String E_UNREACHABLE_SOURCE = "UNREACHABLE_SOURCE";

    @NotNull
    public final Integer searchId;
    @NotNull
    public final String body;
    @Nullable
    public final String sourceUrl;
    @Nullable
    public final String helpRequestId;

    public CreateTip(@NotNull Integer searchId, @NotNull String body, @Nullable String sourceUrl, @Nullable String helpRequestId) {
        this.searchId = searchId;
        this.body = body;
        this.sourceUrl = sourceUrl;
        this.helpRequestId = helpRequestId;
    }
}
