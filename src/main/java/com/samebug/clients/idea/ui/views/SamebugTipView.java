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
import com.samebug.clients.idea.ui.views.components.*;
import com.samebug.clients.idea.ui.views.components.MarkPanel;
import com.samebug.clients.search.api.entities.legacy.BreadCrumb;
import com.samebug.clients.search.api.entities.legacy.RestHit;
import com.samebug.clients.search.api.entities.legacy.Tip;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 3/29/16.
 */
public class SamebugTipView extends JPanel {
    final RestHit<Tip> tip;
    final java.util.List<BreadCrumb> searchBreadcrumb;

    public final LegacyBreadcrumbBar breadcrumbPanel;
    public final TipText tipLabel;
    public final JPanel sourceReferencePanel;
    public final AvatarPanel avatarPanel;
    public final MarkPanel markPanel;

    public SamebugTipView(RestHit<Tip> tip, java.util.List<BreadCrumb> searchBreadcrumb) {
        this.tip = tip;
        this.searchBreadcrumb = searchBreadcrumb;

        breadcrumbPanel = new LegacyBreadcrumbBar(searchBreadcrumb.subList(0, tip.matchLevel));
        tipLabel = new TipText(tip.solution.tip);
        sourceReferencePanel = new TipSourceReferencePanel(tip.solution);
        avatarPanel = new AvatarPanel(tip.solution.author);
        markPanel = new MarkPanel(tip.score, tip.userVoteId != null);

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
                        add(new JPanel() {
                            {
                                setLayout(new GridBagLayout());
                                setOpaque(false);
                                GridBagConstraints gbc = new GridBagConstraints();
                                add(markPanel, gbc);
                                gbc.gridx = 2;
                                gbc.weightx = 1;
                                add(new JPanel(), gbc);
                            }
                        }, BorderLayout.SOUTH);
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
                                        add(avatarPanel, BorderLayout.WEST);
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
}
