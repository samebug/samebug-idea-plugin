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
