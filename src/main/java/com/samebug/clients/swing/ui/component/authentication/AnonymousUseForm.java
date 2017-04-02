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
package com.samebug.clients.swing.ui.component.authentication;

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.authentication.IAnonymousUseForm;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AnonymousUseForm extends JComponent implements IAnonymousUseForm {
    final SamebugButton useAnonymously;

    {
        useAnonymously = new UseAnonButton();
        setLayout(new BorderLayout());
        add(useAnonymously);
    }

    @Override
    public void startPost() {
        useAnonymously.changeToLoadingAnimation();
    }

    @Override
    public void failPost(List<FieldError> errors) throws FormMismatchException {
        useAnonymously.revertFromLoadingAnimation();
    }

    @Override
    public void successPost() {
        useAnonymously.revertFromLoadingAnimation();
    }


    final class UseAnonButton extends SamebugButton {
        {
            setFilled(false);
            setText(MessageService.message("samebug.component.authentication.anonymousUse"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        getListener().useAnonymously(AnonymousUseForm.this);
                        String parentName = AnonymousUseForm.this.getParent().getName();
                        String dialogType = parentName.contains("LogIn") ? "SignIn" : "SignUp";
                        TrackingService.trace(Events.registrationSend("anonymous", dialogType));
                    }
                }
            });
        }
    }

    Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
