/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.ui.views.components.tip;

import com.intellij.openapi.application.ApplicationManager;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.views.components.TransparentPanel;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 4/12/16.
 */
public class WriteTip extends JPanel {
    public static final int maxCharacters = 140;
    public static final int minCharacters = 5;
    public static final int maxLines = 7;

    public final JLabel tipTitle;
    public final JLabel tipDescription;
    public final JTextArea tipBody;
    public final LengthCounter lengthCounter;
    public final JLabel sourceTitle;
    public final JLabel sourceDescription;
    public final JTextField sourceLink;
    public final ErrorPanel errorPanel;
    public final JButton cancel;
    public final JButton submit;

    public WriteTip() {
        tipTitle = new TipTitle();
        tipDescription = new DescriptionLabel(SamebugBundle.message("samebug.tip.write.tip.description"));
        tipBody = new TipBody();
        lengthCounter = new LengthCounter();
        sourceTitle = new SourceTitle();
        sourceDescription = new DescriptionLabel(SamebugBundle.message("samebug.tip.write.source.description"));
        sourceLink = new SourceLink();
        errorPanel = new ErrorPanel();
        cancel = new CancelButton();
        submit = new SubmitButton();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new TransparentPanel() {
            {
                add(tipTitle);
            }
        });
        add(new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                add(tipDescription);
            }
        });
        add(new JScrollPane(tipBody));
        add(new TransparentPanel() {
            {
                setLayout(new FlowLayout(FlowLayout.RIGHT));
                add(lengthCounter);
            }
        });
        add(new TransparentPanel() {
            {
                add(sourceTitle);
            }
        });
        add(new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                add(sourceDescription);
            }
        });
        add(new TransparentPanel() {
            {
                add(sourceLink);
            }
        });
        add(new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
                add(errorPanel);
            }
        });
        add(new TransparentPanel() {
            {
                setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 0));
                add(cancel);
                add(submit);
            }
        });

        PromptSupport.setPrompt(SamebugBundle.message("samebug.tip.write.tip.placeholder"), tipBody);
        PromptSupport.setPrompt(SamebugBundle.message("samebug.tip.write.source.placeholder"), sourceLink);
        updateSubmitButton(false);

        ((AbstractDocument) tipBody.getDocument()).setDocumentFilter(new TipConstraints());
        tipBody.getDocument().addDocumentListener(new TipEditorListener());
    }

    @Override
    public Color getBackground() {
        return ColorUtil.writeTipPanel();
    }

    class TipTitle extends JLabel {
        {
            setText(SamebugBundle.message("samebug.tip.write.tip.title"));
            final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.SIZE, 20);
            setFont(getFont().deriveFont(attributes));
        }
    }

    class SourceTitle extends JLabel {
        {
            setText(SamebugBundle.message("samebug.tip.write.source.title"));
            final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.SIZE, 16);
            setFont(getFont().deriveFont(attributes));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        }
    }

    class DescriptionLabel extends JLabel {
        public DescriptionLabel(final String label) {
            super(label);
        }
    }

    class TipBody extends JTextArea {
        {
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setRows(4);
            setLineWrap(true);
            setWrapStyleWord(true);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        }

        @Override
        public Color getBackground() {
            return ColorUtil.highlightPanel();
        }
    }

    class SourceLink extends JTextField {
        {
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        @Override
        public Color getBackground() {
            return ColorUtil.highlightPanel();
        }
    }

    class LengthCounter extends JLabel {
        int length;

        {
            length = 0;
            setText(String.format("%d/%d", length, maxCharacters));
        }

        @Override
        public Color getForeground() {
            if (minCharacters <= length && length <= maxCharacters) {
                return ColorUtil.unemphasizedText();
            } else {
                return ColorUtil.alertText();
            }
        }

        public void updateLength(final int length) {
            assert (length >= 0);
            this.length = length;
            setText(String.format("%d/%d", length, maxCharacters));
        }
    }

    class ErrorPanel extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(Color.red);
            setVisible(false);
        }
    }

    class CancelButton extends JButton {
        {
            setFocusable(false);
            setText(SamebugBundle.message("samebug.tip.write.cancel"));
        }

        @Override
        public Color getBackground() {
            return ColorUtil.writeTipPanel();
        }
    }

    class SubmitButton extends JButton {
        {
            setFocusable(false);
        }
        @Override
        public Color getBackground() {
            return ColorUtil.writeTipPanel();
        }
    }

    class TipConstraints extends DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
            super.replace(fb, offs, length, str, a);
            final int textSize = fb.getDocument().getLength();
            String text = fb.getDocument().getText(0, textSize);
            int lineBreaks = StringUtils.countMatches(text, System.lineSeparator());
            if (lineBreaks >= maxLines) {
                int lastLineBreakIndex = StringUtils.ordinalIndexOf(text, System.lineSeparator(), maxLines);
                remove(fb, lastLineBreakIndex, text.length() - lastLineBreakIndex);
            }

        }
    }

    class TipEditorListener implements DocumentListener {
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

    void updateSubmitButton(boolean working) {
        if (working) {
            submit.setText("work in progress");
            setEnabled(false);
        } else {
            submit.setText(SamebugBundle.message("samebug.tip.write.submit"));
            setEnabled(true);
        }
    }

    public void beginPostTip() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        updateSubmitButton(true);

    }

    public void finishPostTipWithError(final String message) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        errorPanel.removeAll();
        errorPanel.add(new JLabel(message));
        errorPanel.setVisible(true);
        updateSubmitButton(false);
        revalidate();
        repaint();
    }

    public void finishPostTipWithSuccess() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        updateSubmitButton(false);
    }
}
