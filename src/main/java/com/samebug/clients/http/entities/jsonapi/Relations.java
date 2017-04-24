package com.samebug.clients.http.entities.jsonapi;

import org.jetbrains.annotations.NotNull;

public final class Relations {
    private Boolean hasTips;
    private Boolean hasExternalSolutions;
    private Boolean hasBugmates;
    private Boolean hasHelpRequests;

    @NotNull
    public Boolean getHasTips() {
        return hasTips;
    }

    @NotNull
    public Boolean getHasHelpRequests() {
        return hasHelpRequests;
    }

    @NotNull
    public Boolean getHasExternalSolutions() {
        return hasExternalSolutions;
    }

    @NotNull
    public Boolean getHasBugmates() {
        return hasBugmates;
    }
}
