package com.samebug.clients.http.client;

import com.samebug.clients.http.entities.profile.UserInfo;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.exceptions.UnableToPrepareUrl;
import org.junit.Assert;
import org.junit.Test;

public class GetResponseTest extends TestWithSamebugClient {
    @Test
    public void getUserInfo() throws UnableToPrepareUrl, SamebugClientException {
        UserInfo r = client.getUserInfo("");
        Assert.assertEquals(r.getDisplayName(), "");
    }

}
