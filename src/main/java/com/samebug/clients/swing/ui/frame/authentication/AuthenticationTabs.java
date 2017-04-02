/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.frame.authentication;

import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.tabbedPane.CenterTabbedPaneUI;
import com.samebug.clients.swing.ui.base.tabbedPane.LabelTabHeader;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabHeader;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabbedPane;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class AuthenticationTabs extends SamebugTabbedPane {
    {
        final SignUpTab signUpTab = new SignUpTab();
        final LogInTab logInTab = new LogInTab();
        final SamebugTabHeader signUpTabHeader = addLabeledTab(MessageService.message("samebug.frame.authentication.signUp.tabName"), signUpTab);
        final SamebugTabHeader logInTabHeader = addLabeledTab(MessageService.message("samebug.frame.authentication.logIn.tabName"), logInTab);
        setSelectedIndex(1);

        addChangeListener(new TabChangeTracker());
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

    final class TabChangeTracker implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            String dialogType = getSelectedIndex() == 0 ? "SignUp" : "SignIn";
            TrackingService.trace(Events.registrationDialogSwitched(dialogType));
        }
    }
}
