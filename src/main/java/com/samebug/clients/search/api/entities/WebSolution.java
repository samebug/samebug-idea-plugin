package com.samebug.clients.search.api.entities;

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by poroszd on 3/29/16.
 */
public class WebSolution extends Solution {
    public int solutionId;
    public URL samebugSolutionUrl;
    public URL solutionUrl;
    public String title;
    public Date updated;
    public String sourceName;
    public URL sourceUrl;
    public URL sourceIconUrl;
    public String sourceAuthorName;
    public URL sourceAuthorUrl;
    public Exception exception;
    public List<ComponentStack> componentStack;
}
