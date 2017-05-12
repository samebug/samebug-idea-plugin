package com.samebug.clients.http.client.get;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.jsonapi.IncomingHelpRequestList;
import com.samebug.clients.http.entities.search.StackTraceInfo;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class HelpRequestTest extends TestWithSamebugClient {
    @Test
    public void getIncomingHelpRequests() throws Exception {
        final IncomingHelpRequestList helpRequests = authenticatedClient.getIncomingHelpRequests();
        assertThat(helpRequests.getMeta().getTotal(), equalTo(2));
    }

    @Test
    public void getMyHelpRequest() throws Exception {
        final HelpRequest helpRequest = authenticatedClient.getHelpRequest("58fb434e4f679231ebde3b5c");
        assertThat(helpRequest.getRequester().getDisplayName(), equalTo("testuser"));
        assertThat(helpRequest.getSearchGroup().getLastSearchId(), equalTo(5645));
        assertThat(helpRequest.getSearchGroup().getLastSearchInfo(), instanceOf(StackTraceInfo.class));
    }

    @Test
    public void getOthersHelpRequest() throws Exception {
        final HelpRequest helpRequest = authenticatedClient.getHelpRequest("58fb403d4f679231ebde3b55");
        assertThat(helpRequest.getRequester().getDisplayName(), equalTo("rp"));
        assertThat(helpRequest.getSearchGroup().getNumberOfSearches(), equalTo(1));
        assertThat(helpRequest.getSearchGroup().getLastSearchInfo(), nullValue());
    }
}
