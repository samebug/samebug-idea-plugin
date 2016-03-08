package com.samebug.clients.search.api.entities;

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by poroszd on 3/8/16.
 */
public class ExceptionSearch {
    public int searchId;
    public URL searchUrl;
    public Date timestamp;
    public Exception exception;
    public List<ComponentStack> componentStack;
}
