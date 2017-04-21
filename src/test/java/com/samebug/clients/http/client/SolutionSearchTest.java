package com.samebug.clients.http.client;

import com.samebug.clients.http.entities2.jsonapi.JsonResource;
import com.samebug.clients.http.entities2.jsonapi.TotalItems;
import com.samebug.clients.http.entities2.search.SearchHit;
import com.samebug.clients.http.entities2.solution.ReadableSolution;
import com.samebug.clients.http.entities2.solution.SolutionSlot;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SolutionSearchTest extends TestWithSamebugClient {
    @Test
    public void searchForExternalSolutions() throws Exception {
        JsonResource<List<SearchHit>, TotalItems> solutions = authenticatedClient.getSolutions(5642);
        final TotalItems meta = solutions.meta;
        final SolutionSlot s = solutions.data.get(0).getSolution();
        Assert.assertEquals(1, meta.getTotal().intValue());
        Assert.assertTrue(s instanceof ReadableSolution);
        Assert.assertEquals(88424, s.getId().intValue());
    }

    @Test
    public void searchForTips() throws Exception {
        JsonResource<List<SearchHit>, TotalItems> solutions = authenticatedClient.getTips(5641);
        final TotalItems meta = solutions.meta;
        Assert.assertEquals(301986, meta.getTotal().intValue());
    }
}
