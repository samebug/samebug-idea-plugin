package com.samebug.clients.http.entities.mark;

public final class MarkCreated {
    private Mark mark;
    private Votes votes;

    public MarkCreated(Mark mark, Votes votes) {
        this.mark = mark;
        this.votes = votes;
    }

    public Mark getMark() {
        return mark;
    }

    public Votes getVotes() {
        return votes;
    }
}
