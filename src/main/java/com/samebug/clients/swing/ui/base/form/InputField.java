package com.samebug.clients.swing.ui.base.form;

import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.FontService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

// TODO extract common code with other form fields, like border painting
public class InputField extends JTextField {
    public static final String ERROR_PROPERTY = "samebug.error";

    protected FormColors myColors;
    protected boolean hasError = false;

    {
        myColors = ColorService.NormalForm;
        setFont(FontService.regular(16));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setError(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

    }

    public void setError(boolean hasError) {
        firePropertyChange(ERROR_PROPERTY, this.hasError, hasError);
        this.hasError = hasError;
        repaint();
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = DrawService.init(g);

        Color borderColor;
        if (hasError) borderColor = ColorService.forCurrentTheme(myColors.errorBorder);
        else if (hasFocus()) borderColor = ColorService.forCurrentTheme(myColors.focusBorder);
        else borderColor = ColorService.forCurrentTheme(myColors.normalBorder);
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawService.RoundingDiameter, DrawService.RoundingDiameter);
    }
}
