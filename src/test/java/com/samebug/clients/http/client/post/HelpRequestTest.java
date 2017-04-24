package com.samebug.clients.http.client.post;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.form.HelpRequestCancel;
import com.samebug.clients.http.form.HelpRequestCreate;
import org.junit.Assert;
import org.junit.Test;

public class HelpRequestTest extends TestWithSamebugClient {
    @Test
    public void createHelpRequest() throws Exception {
        try {
            authenticatedClient.createHelpRequest(new HelpRequestCreate.Data(5645, "xxx"));
            Assert.fail();
        } catch (HelpRequestCreate.BadRequest b) {
            Assert.assertArrayEquals(new HelpRequestCreate.ErrorCode[]{HelpRequestCreate.ErrorCode.NO_SUCH_SEARCH}, b.errorList.getErrorCodes().toArray());
        }
    }


    @Test
    public void revokeHelpRequest() throws Exception {
        try {
            authenticatedClient.revokeHelpRequest("58fb434e4f679231ebde3b5c");
            Assert.fail();
        } catch (HelpRequestCancel.BadRequest b) {
            Assert.assertArrayEquals(new HelpRequestCreate.ErrorCode[]{HelpRequestCreate.ErrorCode.NO_SUCH_SEARCH}, b.errorList.getErrorCodes().toArray());
        }
    }
}
