package com.samebug.clients.search.api.entities;

import java.net.URL;
import java.util.List;

/**
 * Created by poroszd on 3/29/16.
 */
public class Solution {
    public int solutionId;
    public URL samebugSolutionUrl;
    public String sourceName;
    public URL sourceUrl;
    public URL sourceIconUrl;
    public String sourceAuthorName;
    public URL sourceAuthorUrl;
    public int numberOfMarks;
    public boolean markedByViewer;
    public Exception exception;
    public List<ComponentStack> componentStack;
}
