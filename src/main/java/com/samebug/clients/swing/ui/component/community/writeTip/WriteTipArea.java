/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.component.community.writeTip;

import com.samebug.clients.common.ui.modules.TextService;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class WriteTipArea extends JComponent {
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

            int lineBreaks = StringUtils.countMatches(text, TextService.lineSeparator);
            if (lineBreaks >= MaxLines) {
                int lastLineBreakIndex = StringUtils.ordinalIndexOf(text, TextService.lineSeparator, MaxLines);
                remove(fb, lastLineBreakIndex, textSize - lastLineBreakIndex);
            }
            if (textSize >= MaxOvershootCharacters) {
                remove(fb, MaxOvershootCharacters, textSize - MaxOvershootCharacters);
            }

        }
    }

    final class EditableArea extends JTextArea {
        private Color[] foregroundColors;
        private Color[] promptColors;

        {
            setRows(DefaultLines);
            setLineWrap(true);
            setWrapStyleWord(true);
            PromptSupport.setPrompt(MessageService.message("samebug.component.tip.write.placeholder", peopleToHelp), this);

            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            promptColors = ColorService.TipFieldPlaceholder;
            setForeground(ColorService.TipFieldText);
            setFont(FontService.regular(16));
        }

        public void setForeground(Color[] c) {
            foregroundColors = c;
            updateColors();
        }

        public void updateColors() {
            Color currentColor = ColorService.forCurrentTheme(foregroundColors);
            setCaretColor(currentColor);
            PromptSupport.setForeground(ColorService.forCurrentTheme(promptColors), this);
            super.setForeground(currentColor);
        }

        @Override
        public void updateUI() {
            setUI(new BasicTextAreaUI());
            if (foregroundColors != null) updateColors();
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

            setLayout(new MigLayout("fillx", "10[]10", "10[]10[]6"));
            add(editableArea, "cell 0 0, wmin 0, growx");
            add(lengthCounter, "cell 0 1, align right");

            setBackground(ColorService.TipFieldBackground);


            ((AbstractDocument) editableArea.getDocument()).setDocumentFilter(new TipConstraints());
            editableArea.getDocument().addDocumentListener(new TipEditorListener());
            editableArea.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
            // Make the whole bordered area behave like a text area
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    editableArea.grabFocus();
                }
            });
        }

        @Override
        public void paintBorder(Graphics g) {
            Graphics2D g2 = DrawService.init(g);

            Color borderColor;
            // TODO error
            if (editableArea.hasFocus()) borderColor = ColorService.forCurrentTheme(ColorService.TipFieldFocusBorder);
            else borderColor = ColorService.forCurrentTheme(ColorService.TipFieldNormalBorder);
            g2.setColor(borderColor);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
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
