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
package com.samebug.clients.idea.ui.components;

import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 4/1/16.
 */
public class ExceptionMessageLabel extends JLabel {
    public ExceptionMessageLabel(@Nullable String message) {
        {
            final String escapedText;
            if (message == null) {
                escapedText = String.format("<html><i>%s</i></html>", SamebugBundle.message("samebug.exception.noMessage"));
            } else {
                // Escape html, but keep line breaks
                String broken = StringEscapeUtils.escapeHtml(message).replaceAll("\\n", "<br>");
                escapedText = String.format("<html>%s</html>", broken);
            }

            setFont(UIManager.getFont("TextArea.font"));
            setText(escapedText);
            setVerticalAlignment(SwingConstants.TOP);
        }

    }

    @Override
    public Color getForeground() {
        return ColorUtil.emphasizedText();
    }
}
