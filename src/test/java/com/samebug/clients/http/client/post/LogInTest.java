package com.samebug.clients.http.client.post;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.form.LogIn;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class LogInTest extends TestWithSamebugClient {
    @Test
    public void logInWithInvalidCredentials() throws Exception {
        try {
            unauthenticatedClient.logIn(new LogIn.Data("xxx", "xxx"));
            Assert.fail();
        } catch (LogIn.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(LogIn.ErrorCode.INVALID_CREDENTIALS));
        }
    }

    @Test
    public void logInWithValidCredentials() throws Exception {
        AuthenticationResponse r = unauthenticatedClient.logIn(new LogIn.Data("testuser@samebug.io", "123456"));
        assertThat(r.getUser().getDisplayName(), equalTo("testuser"));
    }
}
