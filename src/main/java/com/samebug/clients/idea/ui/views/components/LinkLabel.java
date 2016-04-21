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
package com.samebug.clients.idea.ui.views.components;

import com.intellij.ide.BrowserUtil;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.tracking.Events;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by poroszd on 3/29/16.
 */
public class LinkLabel extends JLabel {
    final private String text;
    final private URL link;

    public LinkLabel(final String text, final URL link) {
        super(text);
        this.text = text;
        this.link = link;
        if (link != null) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BrowserUtil.browse(link);
                    // TODO get project from data context?
                    Tracking.appTracking().trace(Events.linkClick(null, link));
                }
            });
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            setFont(getFont().deriveFont(attributes));
        }
    }
}
