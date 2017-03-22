package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.frame.authentication.AuthenticationFrame;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class AuthenticationFrameTest extends TestDialog {
    @Test
    public void authentication() {
        waitToInitializeUI(null).showDialog();
    }

    public void initializeUI(String resourceJson) throws Exception {
        AuthenticationFrame f = new AuthenticationFrame();
        setContentPane(f);
    }
}
