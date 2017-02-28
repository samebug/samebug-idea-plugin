package com.samebug.clients.swing.ui.component.authentication;

import com.samebug.clients.common.ui.component.authentication.IAuthenticationFrame;
import com.samebug.clients.swing.ui.component.util.errorBarPane.ErrorBarPane;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;

// TODO
public class AuthenticationFrame extends ErrorBarPane implements IAuthenticationFrame {
    public AuthenticationFrame() {
        addMainComponent(new SamebugLabel("TODO login screen"));
    }
}
