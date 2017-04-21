package com.samebug.clients.http.client;

import com.samebug.clients.http.entities2.jsonapi.JsonResource;
import com.samebug.clients.http.entities2.jsonapi.Relations;
import com.samebug.clients.http.entities2.response.CreateSearch;
import com.samebug.clients.http.entities2.response.GetSearch;
import com.samebug.clients.http.entities2.search.Search;
import com.samebug.clients.http.entities2.search.SearchCreate;
import com.samebug.clients.http.entities2.search.StackTraceSearch;
import com.samebug.clients.http.entities2.search.TextSearch;
import org.junit.Assert;
import org.junit.Test;

public class SearchTest extends TestWithSamebugClient {
    @Test
    public void lameTextSearch() throws Exception {
        CreateSearch r = authenticatedClient.createSearch(new SearchCreate("xxx"));
        Assert.assertTrue(r.getData() instanceof TextSearch);
        TextSearch s = (TextSearch) r.getData();
        Assert.assertEquals("xxx", s.getQuery());
        Assert.assertEquals("testuser", s.getUser().getDisplayName());
    }

    @Test
    public void stacktraceSearch() throws Exception {
        String stacktrace = "java.lang.StringIndexOutOfBoundsException: String index out of range: -1\n" +
                "    at java.lang.String.charAt(String.java:658)\n" +
                "    at Fail.main(Fail.java:6)";
        CreateSearch r = authenticatedClient.createSearch(new SearchCreate(stacktrace));
        Assert.assertTrue(r.getData() instanceof StackTraceSearch);
        StackTraceSearch s = (StackTraceSearch) r.getData();
        Assert.assertEquals("java.lang.StringIndexOutOfBoundsException", s.getExceptionType());
        Assert.assertEquals("testuser", s.getUser().getDisplayName());
    }

    @Test
    public void getSearch() throws Exception {
        GetSearch search = authenticatedClient.getSearch(5641);
        Assert.assertTrue(search.getData() instanceof StackTraceSearch);
        StackTraceSearch s = (StackTraceSearch) search.getData();
        Assert.assertEquals("testuser", s.getUser().getDisplayName());
    }
}
