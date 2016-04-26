/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.views.components;

import com.intellij.ide.BrowserUtil;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.tracking.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.util.HashMap;

public class LinkLabel extends JLabel {
    @Nullable
    MouseListener myMouseListener;

    public LinkLabel(@NotNull final String text, @Nullable final URL link) {
        super(text);
        setLink(link);
    }

    public void setLink(@Nullable final URL link) {
        removeMouseListener(myMouseListener);
        if (link != null) {
            myMouseListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BrowserUtil.browse(link);
                    // TODO get project from data context?
                    Tracking.appTracking().trace(Events.linkClick(null, link));
                }
            };
            addMouseListener(myMouseListener);

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            setFont(getDefaultFont().deriveFont(attributes));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            setFont(getDefaultFont());
        }
    }

    static Font getDefaultFont() {
        return UIManager.getFont("Label.font");
    }
}
