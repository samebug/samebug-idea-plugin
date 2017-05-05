package com.samebug.clients.http.client.post;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.helprequest.NewHelpRequest;
import com.samebug.clients.http.exceptions.UserUnauthorized;
import com.samebug.clients.http.form.HelpRequestCancel;
import com.samebug.clients.http.form.HelpRequestCreate;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HelpRequestTest extends TestWithSamebugClient {
    private static String testHelpRequestId = null;

    @Test
    public void t01createHelpRequestOnAnOtherUsersSearch() throws Exception {
        try {
            authenticatedClient.createHelpRequest(5644, new NewHelpRequest("xxx"));
            Assert.fail();
        } catch (UserUnauthorized ignored) {
        }
    }

    @Test
    public void t02createHelpRequestWithALongContext() throws Exception {
        String context = StringUtils.repeat("x", 257);
        try {
            authenticatedClient.createHelpRequest(5641, new NewHelpRequest(context));
            Assert.fail();
        } catch (HelpRequestCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(HelpRequestCreate.ErrorCode.CONTEXT_TOO_LONG));
        }
    }

    @Test
    public void t03createHelpRequestOnTextSearch() throws Exception {
        try {
            authenticatedClient.createHelpRequest(5646, new NewHelpRequest("x"));
            Assert.fail();
        } catch (HelpRequestCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(HelpRequestCreate.ErrorCode.NOT_STACKTRACE_SEARCH));
        }
    }

    @Test
    public void t04createHelpRequest() throws Exception {
        final HelpRequest helpRequest = authenticatedClient.createHelpRequest(5641, new NewHelpRequest("x"));
        testHelpRequestId = helpRequest.getId();
        assertThat(helpRequest.getRequester().getDisplayName(), equalTo("testuser"));
        assertThat(helpRequest.getWorkspaceId(), equalTo(4));
    }

    @Test
    public void t05createHelpRequestWhereItAlreadyExists() throws Exception {
        try {
            authenticatedClient.createHelpRequest(5641, new NewHelpRequest("x"));
            Assert.fail();
        } catch (HelpRequestCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(HelpRequestCreate.ErrorCode.DUPLICATE_HELP_REQUEST));
        }
    }


    @Test
    public void t06revokeHelpRequest() throws Exception {
        assert testHelpRequestId != null;
        final HelpRequest revokedHelpRequest = authenticatedClient.cancelHelpRequest(testHelpRequestId);
        assertThat(revokedHelpRequest.getId(), equalTo(testHelpRequestId));
    }

    @Test
    public void t07revokeAlreadyRevokedHelpRequest() throws Exception {
        assert testHelpRequestId != null;
        try {
            authenticatedClient.cancelHelpRequest(testHelpRequestId);
            Assert.fail();
        } catch (HelpRequestCancel.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(HelpRequestCancel.ErrorCode.ALREADY_DEACTIVATED));
        }
    }
}
