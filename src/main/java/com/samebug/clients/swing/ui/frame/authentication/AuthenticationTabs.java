package com.samebug.clients.swing.ui.frame.authentication;

import com.samebug.clients.swing.ui.base.tabbedPane.CenterTabbedPaneUI;
import com.samebug.clients.swing.ui.base.tabbedPane.LabelTabHeader;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabHeader;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabbedPane;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.awt.*;

public class AuthenticationTabs extends SamebugTabbedPane {
    {
        final SignUpTab signUpTab = new SignUpTab();
        final LogInTab logInTab = new LogInTab();
        final SamebugTabHeader signUpTabHeader = addLabeledTab(MessageService.message("samebug.frame.authentication.signUp.tabName"), signUpTab);
        final SamebugTabHeader logInTabHeader = addLabeledTab(MessageService.message("samebug.frame.authentication.logIn.tabName"), logInTab);
        setSelectedIndex(1);
    }

    @Override
    public void updateUI() {
        setBackground(ColorService.forCurrentTheme(ColorService.Background));
        setUI(new CenterTabbedPaneUI());
    }

    // NOTE do not add border to the label, so they can be center aligned properly
    public SamebugTabHeader addLabeledTab(String tabName, Component tabComponent) {
        SamebugTabHeader tabHeader = new LabelTabHeader(tabName);

        int newTabIndex = getTabCount();
        super.addTab(null, tabComponent);
        setTabComponentAt(newTabIndex, tabHeader);
        return tabHeader;
    }
}
