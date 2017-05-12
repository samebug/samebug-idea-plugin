package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.base.form.InputField;
import com.samebug.clients.swing.ui.component.authentication.FormField;
import com.samebug.clients.swing.ui.component.authentication.LogInForm;
import com.samebug.clients.swing.ui.frame.authentication.AuthenticationFrame;
import com.samebug.clients.swing.ui.frame.authentication.AuthenticationTabs;
import com.samebug.clients.swing.ui.frame.authentication.LogInTab;
import org.junit.Test;

import javax.swing.*;

public class AuthenticationFrameTest extends TestDialog {
    @Test
    public void authentication() {
        waitToInitializeUI(null).showDialog();
    }

    @Test
    public void diplayInternationalCharacters() {
        TestDialog d = waitToInitializeUI(null);
        AuthenticationTabs authenticationTabs = (AuthenticationTabs) ((JComponent) ((JRootPane) d.getComponent(0)).getContentPane().getComponent(0)).getComponent(2);
        LogInForm logInForm = ((LogInForm) ((LogInTab) authenticationTabs.getComponent(2)).getComponent(0));
        InputField email = (InputField) ((FormField) logInForm.getComponent(0)).getComponent(1);


        email.setText(korean + " - " + hungarian + " - " + japan + " - " + vietnamese + " - " + turkish + " - " + chinese + " - " + russian);
        d.showDialog();
    }

    public void initializeUI(String resourceJson) throws Exception {
        AuthenticationFrame f = new AuthenticationFrame();
        setContentPane(f);
    }

    private final String korean = "\uae40\ubc94\uc900";
    private final String chinese = "\u67af\u9ec4\u53f6\u5b50";
    private final String japan = "Bão Cát";
    private final String vietnamese = "Trần Minh Tuấn";
    private final String russian = "Сергей Колченко";
    private final String turkish = "Emrah Küçük";
    private final String hungarian = "áíőű ÁÍŰŐ";


}
