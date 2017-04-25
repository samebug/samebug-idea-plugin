package com.samebug.clients.http.client.get;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.helprequest.IncomingHelpRequestList;
import com.samebug.clients.http.entities.search.ReadableSearchGroup;
import com.samebug.clients.http.entities.search.SearchableSearchGroup;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class HelpRequestTest extends TestWithSamebugClient {
    @Test
    public void getIncomingHelpRequests() throws Exception {
        final IncomingHelpRequestList helpRequests = authenticatedClient.getIncomingHelpRequests();
    }

    @Test
    public void getMyHelpRequest() throws Exception {
        final HelpRequest helpRequest = authenticatedClient.getHelpRequest("58fb434e4f679231ebde3b5c");
        assertThat(helpRequest.getRequester().getDisplayName(), equalTo("testuser"));
        assertThat(helpRequest.getSearchGroup(), instanceOf(ReadableSearchGroup.class));
        assertThat(((ReadableSearchGroup) helpRequest.getSearchGroup()).getLastSearchId(), equalTo(5645));
    }

    @Test
    public void getOthersHelpRequest() throws Exception {
        final HelpRequest helpRequest = authenticatedClient.getHelpRequest("58fb403d4f679231ebde3b55");
        assertThat(helpRequest.getRequester().getDisplayName(), equalTo("rp"));
        assertThat(helpRequest.getSearchGroup(), instanceOf(SearchableSearchGroup.class));
    }
}
