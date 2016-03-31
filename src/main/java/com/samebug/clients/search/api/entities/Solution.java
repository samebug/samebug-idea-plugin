package com.samebug.clients.search.api.entities;

import java.util.List;

/**
 * Created by poroszd on 3/29/16.
 */
public class Solution {
    public int solutionId;
    public Document document;

    public int numberOfMarks;
    public boolean markedByViewer;

    public Exception exception;
    public List<ComponentStack> componentStack;
}
