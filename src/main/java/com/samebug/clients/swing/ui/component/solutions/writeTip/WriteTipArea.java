package com.samebug.clients.swing.ui.component.solutions.writeTip;

import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import com.samebug.clients.swing.ui.component.util.panel.SamebugPanel;
import com.samebug.clients.swing.ui.global.ColorService;
import com.samebug.clients.swing.ui.global.DrawService;
import com.samebug.clients.swing.ui.global.FontService;
import com.samebug.clients.swing.ui.global.MessageService;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public final class WriteTipArea extends SamebugPanel {
    // TODO is it necessary? anyway, it can still be bypassed by pasting large text
    public static final int MaxOvershootCharacters = 200;
    public static final int MaxCharacters = 140;
    public static final int MinCharacters = 5;
    public static final int DefaultLines = 4;
    public static final int MaxLines = 7;

    final int peopleToHelp;
    final BorderedArea borderedArea;

    public WriteTipArea(int peopleToHelp) {
        this.peopleToHelp = peopleToHelp;
        borderedArea = new BorderedArea();

        setLayout(new MigLayout("fillx", "0[fill]0", "0[]0"));
        add(borderedArea);
    }

    final class TipConstraints extends DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
            super.replace(fb, offs, length, str, a);
            final int textSize = fb.getDocument().getLength();
            String text = fb.getDocument().getText(0, textSize);
            assert text.length() == textSize;

            int lineBreaks = StringUtils.countMatches(text, TextUtil.lineSeparator);
            if (lineBreaks >= MaxLines) {
                int lastLineBreakIndex = StringUtils.ordinalIndexOf(text, TextUtil.lineSeparator, MaxLines);
                remove(fb, lastLineBreakIndex, textSize - lastLineBreakIndex);
            }
            if (textSize >= MaxOvershootCharacters) {
                remove(fb, MaxOvershootCharacters, textSize - MaxOvershootCharacters);
            }

        }
    }
    final class EditableArea extends JTextArea {
        private Color[] foregroundColors;
        private Color[] backgroundColors;
        {
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            setRows(DefaultLines);
            setLineWrap(true);
            setWrapStyleWord(true);
            setForeground(ColorService.Text);
            setFont(FontService.regular(16));
        }
        public void setForeground(Color[] c) {
            foregroundColors = c;
            setForeground(ColorService.forCurrentTheme(foregroundColors));
        }

        public void setBackground(Color[] c) {
            backgroundColors = c;
            setBackground(ColorService.forCurrentTheme(backgroundColors));
        }
        @Override
        public void updateUI() {
            setUI(new BasicTextAreaUI());
            setForeground(ColorService.forCurrentTheme(foregroundColors));
            setBackground(ColorService.forCurrentTheme(backgroundColors));
        }
    }
    final class LengthCounter extends SamebugLabel {
        int length;

        {
            setFont(FontService.regular(12));
            setForeground(ColorService.UnemphasizedText);
            updateLength(0);
        }

        public void updateLength(final int length) {
            assert (length >= 0);
            this.length = length;
            setText(String.format("%d", MaxCharacters - length));
        }
    }
    final class BorderedArea extends SamebugPanel {
        final EditableArea editableArea;
        final LengthCounter lengthCounter;

        {
            editableArea = new EditableArea();
            lengthCounter = new LengthCounter();

            setLayout(new MigLayout("fillx", "10[]10", "10[]10[]10"));
            add(editableArea, "cell 0 0, wmin 0, growx");
            add(lengthCounter, "cell 0 1, align right");

            // TODO this won't work with ui changes i think
            PromptSupport.setPrompt(MessageService.message("samebug.component.tip.write.placeholder", peopleToHelp), editableArea);
            ((AbstractDocument) editableArea.getDocument()).setDocumentFilter(new TipConstraints());
            editableArea.getDocument().addDocumentListener(new TipEditorListener());
        }

        @Override
        public void paintBorder(Graphics g) {
            Graphics2D g2 = DrawService.init(g);

            g2.setColor(ColorService.forCurrentTheme(ColorService.Separator));
            g2.drawRect(0,0,getWidth() - 1, getHeight() - 1);
        }


        final class TipEditorListener implements DocumentListener {
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
}
