package com.samebug.clients.swing.ui.base.form;

import com.samebug.clients.common.ui.modules.TextService;
import org.apache.commons.lang.StringUtils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class MaxCharactersAndLinesConstraints extends DocumentFilter {
    private final int MaxCharacters;
    private final int MaxLines;

    public MaxCharactersAndLinesConstraints(int maxCharacters, int maxLines) {
        this.MaxLines = maxLines;
        this.MaxCharacters = maxCharacters;
    }

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

        // TODO is it necessary? anyway, it can still be bypassed by pasting large text
        if (textSize >= MaxCharacters) {
            remove(fb, MaxCharacters, textSize - MaxCharacters);
        }

    }
}
