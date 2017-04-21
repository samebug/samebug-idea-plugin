package com.samebug.clients.http.client;

import com.samebug.clients.http.entities2.jsonapi.JsonResource;
import com.samebug.clients.http.entities2.meta.Relations;
import com.samebug.clients.http.entities2.search.Search;
import com.samebug.clients.http.entities2.search.SearchCreate;
import com.samebug.clients.http.entities2.search.StackTraceSearch;
import com.samebug.clients.http.entities2.search.TextSearch;
import org.junit.Assert;
import org.junit.Test;

public class SearchTest extends TestWithSamebugClient {
    @Test
    public void lameTextSearch() throws Exception {
        final JsonResource<Search, Relations> r = authenticatedClient.createSearch(new SearchCreate("xxx"));
        Assert.assertTrue(r.data instanceof TextSearch);
        TextSearch s = (TextSearch) r.data;
        Assert.assertEquals("xxx", s.getQuery());
        Assert.assertEquals("testuser", s.getUser().getDisplayName());
    }

    @Test
    public void stacktraceSearch() throws Exception {
        String stacktrace = "java.lang.StringIndexOutOfBoundsException: String index out of range: -1\n" +
                "    at java.lang.String.charAt(String.java:658)\n" +
                "    at Fail.main(Fail.java:6)";
        final JsonResource<Search, Relations> r = authenticatedClient.createSearch(new SearchCreate(stacktrace));
        Assert.assertTrue(r.data instanceof StackTraceSearch);
        StackTraceSearch s = (StackTraceSearch) r.data;
        Assert.assertEquals("java.lang.StringIndexOutOfBoundsException", s.getExceptionType());
        Assert.assertEquals("testuser", s.getUser().getDisplayName());
    }

    @Test
    public void getSearch() throws Exception {
        final JsonResource<Search, ?> search = authenticatedClient.getSearch(5641);
        Assert.assertTrue(search.data instanceof StackTraceSearch);
        StackTraceSearch s = (StackTraceSearch) search.data;
        Assert.assertEquals("testuser", s.getUser().getDisplayName());
    }
}
