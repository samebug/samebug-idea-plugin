package com.samebug.clients.search.api.entities.legacy;

import java.net.URL;
import java.util.Date;

/**
 * Created by poroszd on 4/1/16.
 */
public class SolutionReference extends RestSolution {
    public Source source;
    public Author author;
    public Date createdAt;
    public String title;
    public URL url;
}
