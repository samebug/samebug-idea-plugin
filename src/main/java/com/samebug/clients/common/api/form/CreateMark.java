package com.samebug.clients.common.api.form;

import org.jetbrains.annotations.NotNull;

public final class CreateMark {
    public static final String SOLUTION = "solution";
    public static final String SEARCH = "search";

    public static final String E_ALREADY_MARKED = "ALREADY_MARKED";
    public static final String E_NOT_YOUR_SEARCH = "NOT_YOUR_SEARCH";

    @NotNull
    public final Integer searchId;
    @NotNull
    public final Integer solutionId;

    public CreateMark(@NotNull Integer searchId, @NotNull Integer solutionId) {
        this.searchId = searchId;
        this.solutionId = solutionId;
    }
}
