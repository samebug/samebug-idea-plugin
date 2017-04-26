package com.samebug.clients.http.client.post;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.form.SignUp;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class SignUpTest extends TestWithSamebugClient {
    @Test
    public void signUpWithUsedEmail() throws Exception {
        try {
            unauthenticatedClient.signUp(new SignUp.Data("poroszd", "daniel.poroszkai@samebug.io", "123456"));
            Assert.fail();
        } catch (SignUp.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(SignUp.ErrorCode.EMAIL_USED));
        }
    }

    @Test
    public void signUpWithInvalidEmail() throws Exception {
        try {
            unauthenticatedClient.signUp(new SignUp.Data("poroszd", "daniel.p", "123456"));
            Assert.fail();
        } catch (SignUp.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(SignUp.ErrorCode.EMAIL_INVALID));
        }
    }

    @Test
    public void signUpWithLongEmail() throws Exception {
        try {
            String email = StringUtils.repeat("x", 1024);
            unauthenticatedClient.signUp(new SignUp.Data("poroszd", email + "@samebug.io", "123456"));
            Assert.fail();
        } catch (SignUp.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(SignUp.ErrorCode.EMAIL_LONG));
        }
    }

    @Test
    public void signUpWithLongDisplayName() throws Exception {
        try {
            String displayName = StringUtils.repeat("x", 1024);
            unauthenticatedClient.signUp(new SignUp.Data(displayName, "test-4@samebug.io", "123456"));
            Assert.fail();
        } catch (SignUp.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(SignUp.ErrorCode.DISPLAYNAME_LONG));
        }
    }

    @Test
    public void signUpWithEmptyDisplayName() throws Exception {
        try {
            unauthenticatedClient.signUp(new SignUp.Data("", "test-5@samebug.io", "123456"));
            Assert.fail();
        } catch (SignUp.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(SignUp.ErrorCode.DISPLAYNAME_EMPTY));
        }
    }

    @Test
    public void signUpWithEmptyPassword() throws Exception {
        try {
            unauthenticatedClient.signUp(new SignUp.Data("poroszd", "test-6@samebug.io", ""));
            Assert.fail();
        } catch (SignUp.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(SignUp.ErrorCode.PASSWORD_EMPTY));
        }
    }

    @Test
    public void signUpWithManyProblems() throws Exception {
        try {
            unauthenticatedClient.signUp(new SignUp.Data("", "daniel.poroszkai@samebug.io", ""));
            Assert.fail();
        } catch (SignUp.BadRequest b) {
            assertThat(b.errorList.getErrorCodes(), containsInAnyOrder(SignUp.ErrorCode.EMAIL_USED, SignUp.ErrorCode.DISPLAYNAME_EMPTY, SignUp.ErrorCode.PASSWORD_EMPTY));
        }
    }

    @Test
    public void signUp() throws Exception {
        AuthenticationResponse r = unauthenticatedClient.signUp(new SignUp.Data("test-8", "test-8@samebug.io", "123456"));
        assertThat(r.getUser().getDisplayName(), equalTo("test-8"));
    }
}
