package com.samebug.clients.search.api.entities.legacy;

/**
 * Created by poroszd on 4/1/16.
 */
public class RestHit<T extends RestSolution> {
    public int solutionId;
    public T solution;
    public int matchLevel;
    public int score;
    public boolean markedByUser;
}
