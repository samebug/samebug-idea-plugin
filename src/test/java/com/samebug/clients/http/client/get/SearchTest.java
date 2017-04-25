package com.samebug.clients.http.client.get;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.bugmate.BugmateMatch;
import com.samebug.clients.http.entities.jsonapi.TotalItems;
import com.samebug.clients.http.entities.response.GetBugmates;
import com.samebug.clients.http.entities.response.GetSolutions;
import com.samebug.clients.http.entities.response.GetTips;
import com.samebug.clients.http.entities.response.SearchRequest;
import com.samebug.clients.http.entities.search.ReadableSearchGroup;
import com.samebug.clients.http.entities.search.SearchableSearchGroup;
import com.samebug.clients.http.entities.search.StackTraceSearch;
import com.samebug.clients.http.entities.solution.*;
import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class SearchTest extends TestWithSamebugClient {
    @Test
    public void getSearch() throws Exception {
        SearchRequest search = authenticatedClient.getSearch(5641);
        assertThat(search.getData(), instanceOf(StackTraceSearch.class));
        StackTraceSearch s = (StackTraceSearch) search.getData();
        assertThat(s.getUser().getDisplayName(), equalTo("testuser"));
    }


    @Test
    public void searchForExternalSolutions() throws Exception {
        GetSolutions solutions = authenticatedClient.getSolutions(5642);
        final TotalItems meta = solutions.getMeta();
        final SolutionSlot<ExternalDocument> s = solutions.getData().get(0).getSolution();
        assertThat(meta.getTotal(), equalTo(1));
        assertThat(s, instanceOf(ReadableSolution.class));
        assertThat(s.getId(), equalTo(88424));
    }

    @Test
    public void searchForTips() throws Exception {
        GetTips tips = authenticatedClient.getTips(5642);
        final TotalItems meta = tips.getMeta();
        final SolutionSlot<SamebugTip> s = tips.getData().get(0).getSolution();
        assertThat(meta.getTotal(), equalTo(1));
        assertThat(s, instanceOf(SearchableSolution.class));
        assertThat(s.getId(), equalTo(301986));
        assertThat(s.getDocument().getMessage(), equalTo("Hello, I hope this helps"));
    }


    @Test
    public void getBugmates() throws Exception {
        GetBugmates r = authenticatedClient.getBugmates(5641);
        Assert.assertEquals(2, r.getMeta().getTotal().intValue());

        BugmateMatch poroszdMatch = r.getData().get(0);
        assertThat(poroszdMatch.getBugmate(), instanceOf(RegisteredSamebugUser.class));
        assertThat(poroszdMatch.getBugmate().getDisplayName(), equalTo("poroszd"));
        assertThat(poroszdMatch.getMatchingGroup(), instanceOf(ReadableSearchGroup.class));
        assertThat(((ReadableSearchGroup) poroszdMatch.getMatchingGroup()).getLastSearchId(), equalTo(5644));
        assertThat(((ReadableSearchGroup) poroszdMatch.getMatchingGroup()).getHelpRequestId(), equalTo("58fb40604f679231ebde3b58"));

        BugmateMatch rpMatch = r.getData().get(1);
        assertThat(rpMatch.getBugmate(), instanceOf(RegisteredSamebugUser.class));
        assertThat(rpMatch.getBugmate().getDisplayName(), equalTo("rp"));
        assertThat(rpMatch.getMatchingGroup(), instanceOf(SearchableSearchGroup.class));
    }
}
