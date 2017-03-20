package com.samebug.clients.common.api.form;

import org.jetbrains.annotations.NotNull;

public final class CancelMark {
    public static final String MARK = "mark";

    public static final String E_NOT_YOUR_MARK = "NOT_YOUR_MARK";
    public static final String E_ALREADY_CANCELLED = "ALREADY_CANCELLED";

    @NotNull
    public final Integer markId;

    public CancelMark(@NotNull Integer markId) {
        this.markId = markId;
    }
}
