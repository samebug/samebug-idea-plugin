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
package com.samebug.clients.idea.ui.component;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
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

/**
 * A link that will open a browser on click.
 *
 * FIXME: it opens the browser (and also reports tracking) directly, bypassing the controller.
 * If we want to do something more complicated, using events and handling in the controller might be a good idea.
 */
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
                    Project project = DataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(LinkLabel.this));
                    if (project != null) Tracking.projectTracking(project).trace(Events.linkClick(project, link));
                    else Tracking.appTracking().trace(Events.linkClick(null, link));
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
