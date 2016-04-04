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
package com.samebug.clients.idea.ui.views;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.components.AvatarIcon;
import com.samebug.clients.idea.ui.components.LegacyBreadcrumbBar;
import com.samebug.clients.idea.ui.components.LinkLabel;
import com.samebug.clients.idea.ui.components.TipSourceReferencePanel;
import com.samebug.clients.search.api.entities.legacy.BreadCrumb;
import com.samebug.clients.search.api.entities.legacy.RestHit;
import com.samebug.clients.search.api.entities.legacy.Tip;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 3/29/16.
 */
public class SamebugTipView {
    final RestHit<Tip> tip;
    final java.util.List<BreadCrumb> searchBreadcrumb;

    public JPanel controlPanel;
    public ProfilePanel profilePanel;
    public TipLabel tipLabel;
    public TipSourceReferencePanel sourceReferencePanel;
    public JPanel actionPanel;
    public JPanel breadcrumbPanel;
    public TipContentPanel tipContentPanel;

    public SamebugTipView(RestHit<Tip> tip, java.util.List<BreadCrumb> searchBreadcrumb) {
        this.tip = tip;
        this.searchBreadcrumb = searchBreadcrumb;

        controlPanel = new ControlPanel();
        breadcrumbPanel = new LegacyBreadcrumbBar(searchBreadcrumb.subList(0, tip.matchLevel));
        tipLabel = new TipLabel();
        sourceReferencePanel = new TipSourceReferencePanel(tip.solution);
        profilePanel = new ProfilePanel();
        actionPanel = new ActionPanel();
        tipContentPanel = new TipContentPanel();

        controlPanel.add(new JPanel() {
            {
                setLayout(new BorderLayout(0, 0));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.cardSeparator));
                setOpaque(false);
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout(0, 0));
                        setBorder(BorderFactory.createEmptyBorder());
                        setOpaque(false);
                        add(actionPanel, BorderLayout.SOUTH);
                        add(tipContentPanel, BorderLayout.CENTER);
                    }
                }, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);
    }


    public class ControlPanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        }

        @Override
        public Color getBackground() {
            return ColorUtil.highlightPanel();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(400, d.height);
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(Integer.MAX_VALUE, Integer.min(d.height, 250));
        }
    }

    public class TipLabel extends JTextArea {
        {
            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.SIZE, 16);
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            setFont(getFont().deriveFont(attributes));
            setEditable(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setBackground(null);
            setOpaque(false);
            setBorder(null);
            setText(tip.solution.tip);
        }

        @Override
        public Color getForeground() {
            return ColorUtil.emphasizedText();
        }
    }

    public class ProfilePanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            setOpaque(false);
            setPreferredSize(new Dimension(74, 100));
            final Image profile = ImageUtil.getScaled(tip.solution.author.avatarUrl, 64, 64);
            add(new AvatarIcon(profile), BorderLayout.NORTH);
            add(new JPanel() {
                {
                    setLayout(new BorderLayout(0, 0));
                    setBorder(BorderFactory.createEmptyBorder());
                    setOpaque(false);
                    add(new LinkLabel(tip.solution.author.name, tip.solution.author.url) {
                        {
                            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                            attributes.put(TextAttribute.SIZE, 10);
                            setFont(getFont().deriveFont(attributes));
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setHorizontalTextPosition(SwingConstants.CENTER);
                        }
                    }, BorderLayout.NORTH);
                    add(new JPanel() {
                        {
                            setLayout(new BorderLayout(0, 0));
                            setBorder(BorderFactory.createEmptyBorder());
                            setOpaque(false);
                        }
                    }, BorderLayout.CENTER);
                }
            }, BorderLayout.CENTER);
        }
    }

    public class ActionPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setOpaque(false);
        }
    }

    public class TipContentPanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            setOpaque(false);
            add(profilePanel, BorderLayout.WEST);
            add(new JPanel() {
                {
                    setLayout(new BorderLayout(0, 0));
                    setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                    setOpaque(false);
                    add(tipLabel, BorderLayout.NORTH);
                    add(new JPanel() {
                        {
                            setLayout(new BorderLayout(0, 0));
                            setBorder(BorderFactory.createEmptyBorder());
                            setOpaque(false);
                            add(sourceReferencePanel, BorderLayout.NORTH);
                            add(new JPanel() {
                                {
                                    setOpaque(false);
                                }
                            }, BorderLayout.CENTER);
                        }
                    }, BorderLayout.CENTER);
                }
            }, BorderLayout.CENTER);
        }
    }
}
