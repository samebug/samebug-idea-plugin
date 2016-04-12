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

import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 4/12/16.
 */
public class WriteTip extends JPanel {
    public static final int maxCharacters = 140;
    public static final int minCharacters = 5;
    public static final int maxLines = 7;

    final JLabel tipTitle;
    final JLabel tipDescription;
    final TipBody tipBody;
    final LengthCounter lengthCounter;
    final JLabel sourceTitle;
    final JLabel sourceDescription;
    final SourceLink sourceLink;
    final JButton cancel;
    final JButton submit;

    public WriteTip() {
        tipTitle = new TipTitle();
        tipDescription = new DescriptionLabel(SamebugBundle.message("samebug.tip.write.tip.description"));
        tipBody = new TipBody();
        lengthCounter = new LengthCounter();
        sourceTitle = new SourceTitle();
        sourceDescription = new DescriptionLabel(SamebugBundle.message("samebug.tip.write.source.description"));
        sourceLink = new SourceLink();
        cancel = new CancelButton();
        submit = new SubmitButton();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setOpaque(false);
                add(tipTitle);
            }
        });
        add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                setOpaque(false);
                add(tipDescription);
            }
        });
        add(new JScrollPane(tipBody));
        add(new JPanel() {
            {
                setLayout(new FlowLayout(FlowLayout.RIGHT));
                setBorder(BorderFactory.createEmptyBorder());
                setOpaque(false);
                add(lengthCounter);
            }
        });
        add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setOpaque(false);
                add(sourceTitle);
            }
        });
        add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                setOpaque(false);
                add(sourceDescription);
            }
        });
        add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setOpaque(false);
                add(sourceLink);
            }
        });
        add(new JPanel() {
            {
                setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 0));
                setBorder(BorderFactory.createEmptyBorder());
                setOpaque(false);
                add(cancel);
                add(submit);
            }
        });

        PromptSupport.setPrompt(SamebugBundle.message("samebug.tip.write.tip.placeholder"), tipBody);
        PromptSupport.setPrompt(SamebugBundle.message("samebug.tip.write.source.placeholder"), sourceLink);

        ((AbstractDocument) tipBody.getDocument()).setDocumentFilter(new TipContraints());
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

    class CancelButton extends JButton {
        {
            setText(SamebugBundle.message("samebug.tip.write.cancel"));
        }

        @Override
        public Color getBackground() {
            return ColorUtil.writeTipPanel();
        }
    }

    class SubmitButton extends JButton {
        {
            setText(SamebugBundle.message("samebug.tip.write.submit"));
        }

        @Override
        public Color getBackground() {
            return ColorUtil.writeTipPanel();
        }
    }

    class TipContraints extends DocumentFilter {
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

    public void setActionHandler(final ActionHandler actionHandler) {
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.onCancel();
            }
        });
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.onSubmit();
            }
        });
    }

    public abstract class ActionHandler {
        protected abstract void onCancel();

        protected abstract void onSubmit();

    }
}
