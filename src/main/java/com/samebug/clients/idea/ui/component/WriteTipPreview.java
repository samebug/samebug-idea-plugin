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

import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.component.organism.BreadcrumbBar;
import com.samebug.clients.search.api.entities.StackTraceSearch;
import com.samebug.clients.search.api.entities.Author;

import javax.swing.*;
import java.awt.*;

final public class WriteTipPreview extends WriteTipCTA {
    final JTextArea tipLabel;
    final JPanel profilePanel;
    final JPanel breadcrumbPanel;

    public WriteTipPreview(final Author author, final StackTraceSearch search) {
        tipLabel = new TipText(SamebugBundle.message("samebug.tip.write.preview.tip"));
        profilePanel = new AvatarPanel(author);
        ctaButton = new WriteTipButton();
        breadcrumbPanel = new BreadcrumbBar(search.stackTrace.breadCrumbs);


        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder());
                setOpaque(false);
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout());
                        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                        setOpaque(false);
                        add(profilePanel, BorderLayout.WEST);
                        add(new JPanel() {
                            {
                                setLayout(new BorderLayout());
                                setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                                setOpaque(false);
                                add(ctaButton, BorderLayout.CENTER);
                            }
                        }, BorderLayout.SOUTH);
                        add(new JPanel() {
                            {
                                setLayout(new BorderLayout());
                                setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                                setOpaque(false);
                                add(tipLabel, BorderLayout.CENTER);
                            }
                        }, BorderLayout.CENTER);
                    }
                }, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);
    }

    @Override
    public Color getBackground() {
        return ColorUtil.highlightPanel();
    }

    class WriteTipButton extends JButton {
        {
            setText(SamebugBundle.message("samebug.tip.write.preview.cta"));
        }

        @Override
        public Color getBackground() {
            return ColorUtil.highlightPanel();
        }
    }
}
