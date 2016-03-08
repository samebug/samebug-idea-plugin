package com.samebug.clients.search.api.entities;

import java.util.Date;

/**
 * Created by poroszd on 3/7/16.
 */
public class GroupedExceptionSearch {
    public Date firstSeenSimilar;
    public Date lastSeenSimilar;
    public int bestMatchLevel;
    public int highestVote;
    public int numberOfSimilars;
    public int numberOfSolutions;
    public ExceptionSearch lastSearch;
}
