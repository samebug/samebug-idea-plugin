package com.samebug.clients.search.api.entities;

import java.net.URL;
import java.util.Date;
import java.util.List;

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
    public int lastSearchId;
    public URL lastSearchUrl;
    public List<ComponentStack> componentStack;
    public Exception exception;
}
