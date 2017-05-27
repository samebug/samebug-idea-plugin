package com.samebug.clients.http.client.post;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.search.NewSearchHit;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.NewSolution;
import com.samebug.clients.http.entities.solution.NewTip;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.exceptions.UserUnauthorized;
import com.samebug.clients.http.form.TipCreate;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TipTest extends TestWithSamebugClient {
    @Test
    public void createTipOnNotExistingHelpRequest() throws Exception {
        try {
            authenticatedClient.createTip(5641, new NewSearchHit(new NewSolution(new NewTip("great tip", null), "x")));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.UNKNOWN_HELP_REQUEST));
        }
    }

    @Test
    public void createTipWithShortMessage() throws Exception {
        try {
            authenticatedClient.createTip(5641, new NewSearchHit(new NewSolution(new NewTip("uh", null))));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.MESSAGE_TOO_SHORT));
        }
    }

    @Test
    public void createTipWithLongMessage() throws Exception {
        String message = StringUtils.repeat("x", 1024);
        try {
            authenticatedClient.createTip(5641, new NewSearchHit(new NewSolution(new NewTip(message, null))));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.MESSAGE_TOO_LONG));
        }
    }

    @Test
    public void createTipWithUnreachableSource() throws Exception {
        try {
            authenticatedClient.createTip(5641, new NewSearchHit(new NewSolution(new NewTip("great tip", "https://nightly.samebug.com/xxx"))));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.UNREACHABLE_SOURCE));
        }
    }

    @Test
    public void createTipOnTextSearch() throws Exception {
        try {
            authenticatedClient.createTip(5646, new NewSearchHit(new NewSolution(new NewTip("great tip", null))));
            Assert.fail();
        } catch (TipCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(TipCreate.ErrorCode.NOT_STACKTRACE_SEARCH));
        }
    }

    @Test
    public void createTipOnSomeoneElsesSearch() throws Exception {
        try {
            authenticatedClient.createTip(5637, new NewSearchHit(new NewSolution(new NewTip("great tip", null))));
            Assert.fail();
        } catch (UserUnauthorized ignored) {
        }
    }

    @Test
    public void createTipOnMySearch() throws Exception {
        final SearchHit<SamebugTip> tip = authenticatedClient.createTip(5641, new NewSearchHit(new NewSolution(new NewTip("great tip", null))));
        assertThat(tip.getSolution().getDocument().getMessage(), equalTo("great tip"));
    }
}
