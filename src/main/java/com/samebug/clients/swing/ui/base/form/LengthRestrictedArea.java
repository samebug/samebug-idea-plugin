package com.samebug.clients.swing.ui.base.form;

import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.FontService;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTextAreaUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class LengthRestrictedArea extends SamebugPanel implements IFormField {
    protected final EditableArea myEditableArea;
    protected final LengthCounter myLengthCounter;
    protected FormColors myColors;

    protected boolean hasError = false;

    public LengthRestrictedArea(FormColors formColors) {
        myColors = formColors;
        myEditableArea = createEditableArea();
        myLengthCounter = createLengthCounter();

        setBackgroundColor(myColors.background);

        // Make the whole area act as an editable area
        myEditableArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                hasError = false;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                myEditableArea.grabFocus();
            }
        });

        // add documentlistener that updates the length counter
        myEditableArea.getDocument().addDocumentListener(new LengthListener(myLengthCounter));
    }

    protected abstract EditableArea createEditableArea();
    protected abstract LengthCounter createLengthCounter();

    public String getText() {
        return myEditableArea.getText();
    }

    public void setError() {
        hasError = true;
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = DrawService.init(g);

        Color borderColor;
        if (hasError) borderColor = ColorService.forCurrentTheme(myColors.errorBorder);
        else if (myEditableArea.hasFocus()) borderColor = ColorService.forCurrentTheme(myColors.focusBorder);
        else borderColor = ColorService.forCurrentTheme(myColors.normalBorder);
        g2.setColor(borderColor);
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    protected class EditableArea extends JTextArea {
        public EditableArea(String prompt) {
            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setRows(4);
            setBorder(BorderFactory.createEmptyBorder());

            updateColors();
            PromptSupport.setPrompt(prompt, this);
            setFont(FontService.regular(16));
        }

        public void updateColors() {
            Color currentColor = ColorService.forCurrentTheme(myColors.text);
            setCaretColor(currentColor);
            super.setForeground(currentColor);
            PromptSupport.setForeground(ColorService.forCurrentTheme(myColors.placeholder), this);
        }

        @Override
        public void updateUI() {
            setUI(new BasicTextAreaUI());
            if (myColors != null) updateColors();
        }
    }

    protected static class LengthCounter extends SamebugLabel {
        private final int MaxLength;

        public LengthCounter(int maxLength) {
            this.MaxLength = maxLength;
            setFont(FontService.regular(12));
            setForegroundColor(ColorService.UnemphasizedText);
            updateLength(0);
        }

        void updateLength(final int length) {
            assert (length >= 0);
            setText(String.format("%d", MaxLength - length));
        }
    }

    private final static class LengthListener implements DocumentListener {
        private final LengthCounter lengthCounter;
        LengthListener(LengthCounter lengthCounter) {
            this.lengthCounter = lengthCounter;
        }
        @Override
        public void insertUpdate(DocumentEvent e) {
            lengthCounter.updateLength(e.getDocument().getLength());
            lengthCounter.repaint();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            lengthCounter.updateLength(e.getDocument().getLength());
            lengthCounter.repaint();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            lengthCounter.updateLength(e.getDocument().getLength());
            lengthCounter.repaint();
        }
    }
}
