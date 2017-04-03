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
