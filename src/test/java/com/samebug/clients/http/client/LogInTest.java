package com.samebug.clients.http.client;

import com.samebug.clients.http.entities.profile.LoggedInUser;
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
            Assert.assertArrayEquals(b.errorList.getErrorCodes().toArray(), new LogIn.ErrorCode[]{LogIn.ErrorCode.INVALID_CREDENTIALS});
        }
    }

    @Test
    public void logInWithValidCredentials() throws Exception {
        LoggedInUser r = unauthenticatedClient.logIn(new LogIn.Data("testuser@samebug.io", "123456"));
        Assert.assertEquals(r.displayName, "testuser");
    }
}
