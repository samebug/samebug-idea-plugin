package com.samebug.clients.search.api.entities.legacy;

import java.util.Date;

/**
 * Created by poroszd on 4/1/16.
 */
public class GroupedSearchHistory {
    public GroupedSearchKey id;
    public Date firstSeen;
    public Date lastSeen;
    public int numberOfSimilars;
    public int lastSearchId;
    public ExceptionSearch lastSearch;
}
