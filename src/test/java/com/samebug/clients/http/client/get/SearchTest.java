package com.samebug.clients.http.client.get;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.bugmate.BugmateMatch;
import com.samebug.clients.http.entities.jsonapi.BugmateList;
import com.samebug.clients.http.entities.jsonapi.SolutionList;
import com.samebug.clients.http.entities.jsonapi.TipList;
import com.samebug.clients.http.entities.jsonapi.TotalItems;
import com.samebug.clients.http.entities.search.QueryInfo;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.search.SearchGroup;
import com.samebug.clients.http.entities.search.StackTraceInfo;
import com.samebug.clients.http.entities.solution.ExternalDocument;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.entities.solution.SolutionSlot;
import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
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


    @Test
    public void searchForExternalSolutions() throws Exception {
        SolutionList solutions = authenticatedClient.getSolutions(5642);
        final TotalItems meta = solutions.getMeta();
        final SolutionSlot<ExternalDocument> s = solutions.getData().get(0).getSolution();
        assertThat(meta.getTotal(), equalTo(1));
        assertThat(s.getStackTraceInfo(), notNullValue());
        assertThat(s.getId(), equalTo(88424));
    }

    @Test
    public void searchForTips() throws Exception {
        TipList tips = authenticatedClient.getTips(5642);
        final TotalItems meta = tips.getMeta();
        final SolutionSlot<SamebugTip> s = tips.getData().get(0).getSolution();
        assertThat(meta.getTotal(), equalTo(1));
        assertThat(s.getId(), equalTo(301986));
        assertThat(s.getDocument().getMessage(), equalTo("Hello, I hope this helps"));
        assertThat(s.getStackTraceInfo(), nullValue());
    }


    @Test
    public void getBugmates() throws Exception {
        BugmateList r = authenticatedClient.getBugmates(5641);
        Assert.assertEquals(2, r.getMeta().getTotal().intValue());

        BugmateMatch poroszdMatch = r.getData().get(0);
        SearchGroup poroszdGroup = poroszdMatch.getMatchingGroup();
        QueryInfo q = poroszdGroup.getLastSearchInfo();
        assertThat(poroszdMatch.getBugmate(), instanceOf(RegisteredSamebugUser.class));
        assertThat(poroszdMatch.getBugmate().getDisplayName(), equalTo("poroszd"));
        assertThat(q, notNullValue());
        assertThat(q, instanceOf(StackTraceInfo.class));
        StackTraceInfo s = (StackTraceInfo) q;
        assertThat(s.getExceptionType(), equalTo("java.net.ConnectException"));
        assertThat(poroszdGroup.getLastSearchId(), equalTo(5644));
        assertThat(poroszdGroup.getHelpRequestId(), equalTo("58fb40604f679231ebde3b58"));

        BugmateMatch rpMatch = r.getData().get(1);
        assertThat(rpMatch.getBugmate(), instanceOf(RegisteredSamebugUser.class));
        assertThat(rpMatch.getBugmate().getDisplayName(), equalTo("rp"));
        assertThat(rpMatch.getMatchingGroup().getNumberOfSearches(), equalTo(1));
        assertThat(rpMatch.getMatchingGroup().getLastSearchInfo(), nullValue());
    }
}
