package com.samebug.clients.http.client.post;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.entities.solution.SolutionSlot;
import com.samebug.clients.http.form.TipCreate;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TipTest extends TestWithSamebugClient {
    @Test
    public void createTipOnNotExistingHelpRequest() throws Exception {
        try {
            authenticatedClient.createTip(new TipCreate.ForHelpRequest("great tip", null, 5641, "x"));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.UNKNOWN_HELP_REQUEST));
        }
    }

    @Test
    public void createTipWithShortMessage() throws Exception {
        try {
            authenticatedClient.createTip(new TipCreate.ForSearch("uh", null, 5641));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.MESSAGE_TOO_SHORT));
        }
    }

    @Test
    public void createTipWithLongMessage() throws Exception {
        String message = StringUtils.repeat("x", 1024);
        try {
            authenticatedClient.createTip(new TipCreate.ForSearch(message, null, 5641));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.MESSAGE_TOO_LONG));
        }
    }

    @Test
    public void createTipWithUnreachableSource() throws Exception {
        try {
            authenticatedClient.createTip(new TipCreate.ForSearch("great tip", "https://nightly.samebug.com/xxx", 5641));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.UNREACHABLE_SOURCE));
        }
    }

    @Test
    public void createTipOnTextSearch() throws Exception {
        try {
            authenticatedClient.createTip(new TipCreate.ForSearch("great tip", null, 5646));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.NOT_STACKTRACE_SEARCH));
        }
    }

    @Test
    public void createTipOnSolutionClone() throws Exception {
        try {
            authenticatedClient.createTip(new TipCreate.ForExternalSolution("great tip", null, 2366));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.NOT_SEARCHABLE_SOLUTION));
        }
    }

    @Ignore
    public void createTipOnSomeoneElsesSearch() throws Exception {
        final SolutionSlot<SamebugTip> tip = authenticatedClient.createTip(new TipCreate.ForSearch("great tip", null, 5637));
        assertThat(tip.getDocument().getMessage(), equalTo("great tip"));
    }

    @Test
    public void createTipOnMySearch() throws Exception {
        final SolutionSlot<SamebugTip> tip = authenticatedClient.createTip(new TipCreate.ForSearch("great tip", null, 5641));
        assertThat(tip.getDocument().getMessage(), equalTo("great tip"));
    }
}
