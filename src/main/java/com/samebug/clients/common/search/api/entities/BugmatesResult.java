package com.samebug.clients.common.search.api.entities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BugmatesResult {
    @NotNull
    private List<Bugmate> bugmates;
    @NotNull
    private int numberOfOtherBugmates;
    @NotNull
    private boolean evenMoreExists;

    public BugmatesResult(@NotNull List<Bugmate> bugmates, @NotNull int numberOfOtherBugmates, @NotNull boolean evenMoreExists) {
        this.bugmates = bugmates;
        this.numberOfOtherBugmates = numberOfOtherBugmates;
        this.evenMoreExists = evenMoreExists;
    }

    @NotNull
    public List<Bugmate> getBugmates() {
        return bugmates;
    }

    @NotNull
    public int getNumberOfOtherBugmates() {
        return numberOfOtherBugmates;
    }

    @NotNull
    public boolean isEvenMoreExists() {
        return evenMoreExists;
    }
}
