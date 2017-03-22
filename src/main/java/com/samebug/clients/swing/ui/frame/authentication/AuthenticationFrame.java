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

import com.samebug.clients.common.ui.frame.authentication.IAuthenticationFrame;
import com.samebug.clients.swing.ui.base.frame.BasicFrame;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.CenteredMultilineLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class AuthenticationFrame extends BasicFrame implements IAuthenticationFrame {
    public AuthenticationFrame() {
        addMainComponent(new Authentication());
    }

    private final class Authentication extends JComponent {
        Authentication() {
            final SamebugLabel title = new TitleLabel();
            final CenteredMultilineLabel intro = new IntroLabel();
            final JComponent tabs = new AuthenticationTabs();

            setLayout(new MigLayout("fillx", "0[380!]0", "40[]20[]30[]0"));
            add(title, "cell 0 0, al center");
            add(intro, "cell 0 1, growx, wmin 0");
            add(tabs, "cell 0 2, w 260!, al center");
        }
    }

    private final class TitleLabel extends SamebugLabel {
        {
            setText(MessageService.message("samebug.frame.authentication.title"));
            setFont(FontService.demi(24));
            setForegroundColor(ColorService.EmphasizedText);
        }
    }

    private final class IntroLabel extends CenteredMultilineLabel {
        {
            setText(MessageService.message("samebug.frame.authentication.intro"));
            setForeground(ColorService.Text);
            setFont(FontService.regular(16));
        }
    }


    @Override
    protected FrameListener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
