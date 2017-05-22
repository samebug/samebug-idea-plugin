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

import com.samebug.clients.common.tracking.PageTabs;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.component.authentication.AnonymousUseForm;
import com.samebug.clients.swing.ui.component.authentication.Delimeter;
import com.samebug.clients.swing.ui.component.authentication.LogInForm;
import com.samebug.clients.swing.ui.modules.DataService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class LogInTab extends JComponent {
    {
        DataService.putData(this, TrackingKeys.PageTab, PageTabs.Authentication.LogIn);

        final LogInForm logInForm = new LogInForm();
        final Delimeter delimeter = new Delimeter();
        final AnonymousUseForm anonymousUseForm = new AnonymousUseForm();

        setLayout(new MigLayout("fillx", "0[260px!, fill]0", "0[]10px[]10px[]40px"));
        add(logInForm, "cell 0 0");
        add(delimeter, "cell 0 1");
        add(anonymousUseForm, "cell 0 2");
    }
}
