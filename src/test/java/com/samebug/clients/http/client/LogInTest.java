package com.samebug.clients.http.client;

import com.samebug.clients.http.entities2.authentication.AuthenticationResponse;
import com.samebug.clients.http.form.LogIn;
import org.junit.Assert;
import org.junit.Test;

public class LogInTest extends TestWithSamebugClient {
    @Test
    public void logInWithInvalidCredentials() throws Exception {
        try {
            unauthenticatedClient.logIn(new LogIn.Data("xxx", "xxx"));
            Assert.fail();
        } catch (LogIn.BadRequest b) {
            Assert.assertArrayEquals(new LogIn.ErrorCode[]{LogIn.ErrorCode.INVALID_CREDENTIALS}, b.errorList.getErrorCodes().toArray());
        }
    }

    @Test
    public void logInWithValidCredentials() throws Exception {
        AuthenticationResponse r = unauthenticatedClient.logIn(new LogIn.Data("testuser@samebug.io", "123456")).getData();
        Assert.assertEquals("testuser", r.getUser().getDisplayName());
    }
}
