package com.samebug.clients.http.entities.mark;

import org.jetbrains.annotations.NotNull;

public final class Votes {
    private Integer numberOfMarks;
    private Boolean documentCreatedByUser;
    private Integer marksOnSolutionByUser;
    private Integer marksOnSolution;
    private Integer votesOnDocumentByUser;
    private Integer votesOnDocument;

    @NotNull
    public Integer getNumberOfMarks() {
        return numberOfMarks;
    }

    @NotNull
    public Boolean getDocumentCreatedByUser() {
        return documentCreatedByUser;
    }

    @NotNull
    public Integer getMarksOnSolutionByUser() {
        return marksOnSolutionByUser;
    }

    @NotNull
    public Integer getMarksOnSolution() {
        return marksOnSolution;
    }

    @NotNull
    public Integer getVotesOnDocumentByUser() {
        return votesOnDocumentByUser;
    }

    @NotNull
    public Integer getVotesOnDocument() {
        return votesOnDocument;
    }
}
