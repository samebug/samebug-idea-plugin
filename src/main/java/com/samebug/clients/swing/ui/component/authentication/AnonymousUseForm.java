package com.samebug.clients.swing.ui.component.authentication;

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.authentication.IAnonymousUseForm;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;

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

    }

    @Override
    public void failPost(List<FieldError> errors) throws FormMismatchException {

    }

    @Override
    public void successPost() {

    }


    final class UseAnonButton extends SamebugButton {
        {
            setFilled(false);
            setText(MessageService.message("samebug.component.authentication.anonymousUse"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) getListener().useAnonymously(AnonymousUseForm.this);
                }
            });
        }
    }

    Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
