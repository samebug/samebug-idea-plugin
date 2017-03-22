package com.samebug.clients.swing.ui.frame.authentication;

import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.component.authentication.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class SignUpTab extends JComponent {
    {
        final SignUpForm signUpForm = new SignUpForm();
        final Delimeter delimeter = new Delimeter();
        final AnonymousUseForm anonymousUseForm = new AnonymousUseForm();

        setLayout(new MigLayout("fillx", "0[260!, fill]0", "0[]10[]10[]40"));
        add(signUpForm, "cell 0 0");
        add(delimeter, "cell 0 1");
        add(anonymousUseForm, "cell 0 2");
    }
}
