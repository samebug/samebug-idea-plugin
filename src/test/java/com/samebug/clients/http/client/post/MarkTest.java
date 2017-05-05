package com.samebug.clients.http.client.post;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.mark.Mark;
import com.samebug.clients.http.entities.mark.NewMark;
import com.samebug.clients.http.form.MarkCreate;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MarkTest extends TestWithSamebugClient {
    private static Integer testMarkId = null;

    @Test
    public void t01createMarkOnNonExistingSolution() throws Exception {
        try {
            authenticatedClient.postMark(5641, new NewMark(1));
            Assert.fail();
        } catch (MarkCreate.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(MarkCreate.ErrorCode.NO_SUCH_SOLUTION));
        }
    }

    @Test
    public void t02createMark() throws Exception {
        final Mark mark = authenticatedClient.postMark(5641, new NewMark(2843));
        testMarkId = mark.getId();
    }

    @Test
    public void t03cancelMark() throws Exception {
        authenticatedClient.cancelMark(testMarkId);
    }
}
