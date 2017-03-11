package com.samebug.clients.swing.ui.base.form;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class MaxCharactersConstraints extends DocumentFilter {
    private final int MaxCharacters;
    public MaxCharactersConstraints(int maxCharacters) {
        this.MaxCharacters = maxCharacters;
    }

    @Override
    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
        super.replace(fb, offs, length, str, a);
        final int textSize = fb.getDocument().getLength();

        // TODO is it necessary? anyway, it can still be bypassed by pasting large text
        if (textSize >= MaxCharacters) {
            remove(fb, MaxCharacters, textSize - MaxCharacters);
        }
    }
}