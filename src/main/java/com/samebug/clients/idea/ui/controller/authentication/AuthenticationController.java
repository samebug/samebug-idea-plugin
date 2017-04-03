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
package com.samebug.clients.idea.ui.controller.authentication;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.ui.component.authentication.IAnonymousUseForm;
import com.samebug.clients.common.ui.component.authentication.ILogInForm;
import com.samebug.clients.common.ui.component.authentication.ISignUpForm;
import com.samebug.clients.common.ui.frame.authentication.IAuthenticationFrame;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.idea.ui.controller.toolwindow.ToolWindowController;
import com.samebug.clients.swing.ui.frame.authentication.AuthenticationFrame;
import com.samebug.clients.swing.ui.modules.ListenerService;

import javax.swing.*;

public final class AuthenticationController extends BaseFrameController<IAuthenticationFrame> implements Disposable {
    final SignUpListener signUpListener;
    final LogInListener logInListener;
    final AnonymousUseListener anonymousUseListener;

    public AuthenticationController(ToolWindowController twc, Project project) {
        super(twc, project, new AuthenticationFrame());

        JComponent frame = (JComponent) view;
        signUpListener = new SignUpListener(this);
        ListenerService.putListenerToComponent(frame, ISignUpForm.Listener.class, signUpListener);
        logInListener = new LogInListener(this);
        ListenerService.putListenerToComponent(frame, ILogInForm.Listener.class, logInListener);
        anonymousUseListener = new AnonymousUseListener(this);
        ListenerService.putListenerToComponent(frame, IAnonymousUseForm.Listener.class, anonymousUseListener);
    }
}
