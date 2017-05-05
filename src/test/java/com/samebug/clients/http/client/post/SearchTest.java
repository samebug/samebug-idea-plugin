package com.samebug.clients.http.client.post;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.jsonapi.CreatedSearchResource;
import com.samebug.clients.http.entities.search.NewSearch;
import com.samebug.clients.http.entities.search.StackTraceSearch;
import com.samebug.clients.http.entities.search.TextSearch;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class SearchTest extends TestWithSamebugClient {
    @Test
    public void lameTextSearch() throws Exception {
        CreatedSearchResource r = authenticatedClient.createSearch(new NewSearch("xxx"));
        assertThat(r.getData(), instanceOf(TextSearch.class));
        TextSearch s = (TextSearch) r.getData();
        assertThat(s.getQuery(), equalTo("xxx"));
        assertThat(s.getUser().getDisplayName(), equalTo("testuser"));
    }

    @Test
    public void stacktraceSearch() throws Exception {
        String stacktrace = "java.lang.StringIndexOutOfBoundsException: String index out of range: -1\n"
                + "    at java.lang.String.charAt(String.java:658)\n"
                + "    at Fail.main(Fail.java:6)";
        CreatedSearchResource r = authenticatedClient.createSearch(new NewSearch(stacktrace));
        assertThat(r.getData(), instanceOf(StackTraceSearch.class));
        StackTraceSearch s = (StackTraceSearch) r.getData();
        assertThat(s.getExceptionType(), equalTo("java.lang.StringIndexOutOfBoundsException"));
        assertThat(s.getUser().getDisplayName(), equalTo("testuser"));
    }
}
