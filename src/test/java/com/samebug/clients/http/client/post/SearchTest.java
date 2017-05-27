package com.samebug.clients.http.client.post;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.jsonapi.CreatedSearchResource;
import com.samebug.clients.http.entities.search.*;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class SearchTest extends TestWithSamebugClient {
    @Test
    public void lameTextSearch() throws Exception {
        CreatedSearchResource r = authenticatedClient.createSearch(new NewSearch("xxx"));
        Search search = r.getData();
        QueryInfo q = search.getQueryInfo();
        assertThat(q, instanceOf(TextSearchInfo.class));
        TextSearchInfo s = (TextSearchInfo) q;
        assertThat(s.getQuery(), equalTo("xxx"));
        assertThat(search.getUser().getDisplayName(), equalTo("testuser"));
    }

    @Test
    public void stacktraceSearch() throws Exception {
        String stacktrace = "java.lang.StringIndexOutOfBoundsException: String index out of range: -1\n"
                + "    at java.lang.String.charAt(String.java:658)\n"
                + "    at Fail.main(Fail.java:6)";
        CreatedSearchResource r = authenticatedClient.createSearch(new NewSearch(stacktrace));
        Search search = r.getData();
        QueryInfo q = search.getQueryInfo();
        assertThat(q, instanceOf(StackTraceInfo.class));
        StackTraceInfo s = (StackTraceInfo) q;
        assertThat(s.getExceptionType(), equalTo("java.lang.StringIndexOutOfBoundsException"));
        assertThat(search.getUser().getDisplayName(), equalTo("testuser"));
    }
}
