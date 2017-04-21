package com.samebug.clients.http.client;

import com.samebug.clients.http.entities2.jsonapi.TotalItems;
import com.samebug.clients.http.entities2.response.GetSolutions;
import com.samebug.clients.http.entities2.response.GetTips;
import com.samebug.clients.http.entities2.solution.*;
import org.junit.Assert;
import org.junit.Test;

public class SolutionSearchTest extends TestWithSamebugClient {
    @Test
    public void searchForExternalSolutions() throws Exception {
        GetSolutions solutions = authenticatedClient.getSolutions(5642);
        final TotalItems meta = solutions.getMeta();
        final SolutionSlot<ExternalDocument> s = solutions.getData().get(0).getSolution();
        Assert.assertEquals(1, meta.getTotal().intValue());
        Assert.assertTrue("Solution was of wrong type: " + s, s instanceof ReadableSolution);
        Assert.assertEquals(88424, s.getId().intValue());
    }

    @Test
    public void searchForTips() throws Exception {
        GetTips tips = authenticatedClient.getTips(5642);
        final TotalItems meta = tips.getMeta();
        final SolutionSlot<SamebugTip> s = tips.getData().get(0).getSolution();
        Assert.assertEquals(1, meta.getTotal().intValue());
        Assert.assertTrue("Solution was of wrong type: " + s, s instanceof SearchableSolution);
        Assert.assertEquals(301986, s.getId().intValue());
        Assert.assertEquals("Hello, I hope this helps", s.getDocument().getMessage());
    }
}
