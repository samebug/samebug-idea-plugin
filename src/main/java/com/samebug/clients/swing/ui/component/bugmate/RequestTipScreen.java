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
package com.samebug.clients.swing.ui.component.bugmate;

import com.samebug.clients.common.ui.component.form.*;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class RequestTipScreen extends JComponent implements IForm {
    // TODO
    static final String TIP_FIELD_NAME = "tip";

    final WriteRequestArea writeRequestArea;
    final SamebugButton sendButton;
    final LinkLabel cancelButton;

    public RequestTipScreen(final BugmateList bugmateList) {
        writeRequestArea = new WriteRequestArea(bugmateList);
        sendButton = new SamebugButton(MessageService.message("samebug.component.bugmate.ask.send"));
        sendButton.setFilled(true);
        cancelButton = new LinkLabel(MessageService.message("samebug.component.bugmate.ask.cancel"));

        setLayout(new MigLayout("fillx", "0[]0", "0[]10[]10[]0"));
        add(writeRequestArea, "cell 0 0, growx");
        add(sendButton, "cell 0 1, align center");
        add(cancelButton, "cell 0 2, align center");

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bugmateList.getListener().askBugmates(bugmateList, "TODO");
            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bugmateList.requestTip.changeToClosedState();
            }
        });
    }

    @Override
    public void setFormErrors(List<FormError> errors) throws FormMismatchException {
        List<FormError> mismatched = new ArrayList<FormError>();
        for (FormError f : errors) {
            try {
                if (TIP_FIELD_NAME.equals(f.key)) writeRequestArea.setFormError(f.code);
                else throw new FieldNameMismatchException(f.key);
            } catch (ErrorCodeMismatchException e) {
                mismatched.add(f);
            } catch (FieldNameMismatchException e) {
                mismatched.add(f);
            }
        }
        if (!mismatched.isEmpty()) throw new FormMismatchException(mismatched);
        revalidate();
        repaint();
    }
}
