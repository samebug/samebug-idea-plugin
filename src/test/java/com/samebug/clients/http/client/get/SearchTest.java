package com.samebug.clients.http.client.get;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.search.QueryInfo;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.search.StackTraceInfo;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class SearchTest extends TestWithSamebugClient {
    @Test
    public void getSearch() throws Exception {
        Search search = authenticatedClient.getSearch(5641);
        QueryInfo q = search.getQueryInfo();
        assertThat(q, instanceOf(StackTraceInfo.class));
        StackTraceInfo s = (StackTraceInfo) q;
        assertThat(search.getUser().getDisplayName(), equalTo("testuser"));
        assertThat(s.getExceptionType(), equalTo("java.net.ConnectException"));
    }
}
