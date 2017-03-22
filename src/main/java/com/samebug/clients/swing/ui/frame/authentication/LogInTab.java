package com.samebug.clients.swing.ui.frame.authentication;

import com.samebug.clients.swing.ui.component.authentication.AnonymousUseForm;
import com.samebug.clients.swing.ui.component.authentication.Delimeter;
import com.samebug.clients.swing.ui.component.authentication.LogInForm;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class LogInTab extends JComponent {
    {
        final LogInForm logInForm = new LogInForm();
        final Delimeter delimeter = new Delimeter();
        final AnonymousUseForm anonymousUseForm = new AnonymousUseForm();

        setLayout(new MigLayout("fillx", "0[260!, fill]0", "0[]10[]10[]40"));
        add(logInForm, "cell 0 0");
        add(delimeter, "cell 0 1");
        add(anonymousUseForm, "cell 0 2");
    }

}
