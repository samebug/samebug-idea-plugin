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

import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.components.AvatarIcon;
import com.samebug.clients.idea.ui.components.LegacyBreadcrumbBar;
import com.samebug.clients.idea.ui.components.LinkLabel;
import com.samebug.clients.search.api.entities.legacy.BreadCrumb;
import com.samebug.clients.search.api.entities.legacy.RestHit;
import com.samebug.clients.search.api.entities.legacy.Tip;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 3/29/16.
 */
public class SamebugTipView extends JPanel {
    final RestHit<Tip> tip;
    final java.util.List<BreadCrumb> searchBreadcrumb;

    public SamebugTipView(RestHit<Tip> tip, java.util.List<BreadCrumb> searchBreadcrumb) {
        this.tip = tip;
        this.searchBreadcrumb = searchBreadcrumb;

        final JPanel breadcrumbPanel = new LegacyBreadcrumbBar(searchBreadcrumb.subList(0, tip.matchLevel));
        final JTextArea tipLabel = new TipLabel();
        final JPanel sourceReferencePanel = new TipSourceReferencePanel(tip.solution);
        final JPanel profilePanel = new ProfilePanel();
        final JPanel actionPanel = new ActionPanel();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.cardSeparator));
                setOpaque(false);
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout());
                        setBorder(BorderFactory.createEmptyBorder());
                        setOpaque(false);
                        add(actionPanel, BorderLayout.SOUTH);
                        add(new JPanel() {
                            {
                                setLayout(new BorderLayout());
                                setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                                setOpaque(false);
                                add(sourceReferencePanel, BorderLayout.SOUTH);
                                add(new JPanel() {
                                    {
                                        setLayout(new BorderLayout());
                                        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                                        setOpaque(false);
                                        add(profilePanel, BorderLayout.WEST);
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
                }, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);

        setPreferredSize(new Dimension(400, getPreferredSize().height));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.min(getPreferredSize().height, 250)));
    }

    @Override
    public Color getBackground() {
        return ColorUtil.highlightPanel();
    }

    class TipLabel extends JTextArea {
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

    class ProfilePanel extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            setPreferredSize(new Dimension(74, 100));
            final Image profile = ImageUtil.getScaled(tip.solution.author.avatarUrl, 64, 64);
            add(new AvatarIcon(profile), BorderLayout.NORTH);
            add(new LinkLabel(tip.solution.author.name, tip.solution.author.url) {
                {
                    HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                    attributes.put(TextAttribute.SIZE, 10);
                    setFont(getFont().deriveFont(attributes));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setHorizontalTextPosition(SwingConstants.CENTER);
                }
            }, BorderLayout.CENTER);
        }
    }

    class TipSourceReferencePanel extends JPanel {
        public TipSourceReferencePanel(@NotNull Tip tip) {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            if (tip.via == null) {
                // no source, show only tip timestamp
                add(new JLabel(String.format("%s", TextUtil.prettyTime(tip.createdAt))) {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.unemphasizedText();
                    }
                });
            } else if (tip.via.author == null) {
                // source without author
                add(new JLabel(String.format("%s | via ", TextUtil.prettyTime(tip.createdAt))) {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.unemphasizedText();
                    }
                });
                add(new LinkLabel(tip.via.source.name, tip.via.url) {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.emphasizedText();
                    }
                });
            } else {
                // source with author
                add(new JLabel(String.format("%s | ", TextUtil.prettyTime(tip.createdAt))) {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.unemphasizedText();
                    }
                });
                add(new LinkLabel(tip.via.author.name, tip.via.author.url) {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.emphasizedText();
                    }
                });
                add(new JLabel(" via ") {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.unemphasizedText();
                    }
                });
                add(new LinkLabel(tip.via.source.name, tip.via.url) {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.emphasizedText();
                    }
                });
            }
        }
    }

    class ActionPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setOpaque(false);
        }
    }
}
