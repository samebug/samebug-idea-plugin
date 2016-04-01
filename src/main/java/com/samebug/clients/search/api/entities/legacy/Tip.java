package com.samebug.clients.search.api.entities.legacy;

import java.util.Date;

/**
 * Created by poroszd on 4/1/16.
 */
public class Tip extends RestSolution {
    public Author author;
    public Date createdAt;
    public String tip;
    public SolutionReference via;
}
