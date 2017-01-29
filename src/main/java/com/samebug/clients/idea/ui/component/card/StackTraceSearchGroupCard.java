/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.ui.component.card;

import com.samebug.clients.common.entities.ExceptionType;
import com.samebug.clients.common.search.api.entities.SearchGroup;
import com.samebug.clients.common.search.api.entities.StackTraceSearchGroup;
import com.samebug.clients.common.ui.Colors;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.component.ExceptionMessageLabel;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.component.organism.BreadcrumbBar;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;

final public class StackTraceSearchGroupCard extends SearchGroupCard {
    public final StackTraceSearchGroup searchGroup;
    final ExceptionType exceptionType;
    public final JLabel packageLabel;
    public final JLabel hitsLabel;
    public final JLabel titleLabel;
    public final JLabel exceptionMessageLabel;
    public final JPanel breadcrumbPanel;
    @NotNull
    final Actions actions;

    public StackTraceSearchGroupCard(final StackTraceSearchGroup searchGroup, @NotNull final Actions actions) {
        super(searchGroup);
        this.searchGroup = searchGroup;
        this.actions = actions;

        exceptionType = new ExceptionType(searchGroup.getLastSearch().getStackTrace().getTrace().getTypeName());
        packageLabel = new PackageLabel();
        hitsLabel = new HitsLabel();
        titleLabel = new TitleLabel();
        exceptionMessageLabel = new ExceptionMessageLabel(searchGroup.getLastSearch().getStackTrace().getTrace().getMessage());
        breadcrumbPanel = new BreadcrumbBar(searchGroup.getLastSearch().getStackTrace().getBreadCrumbs());

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.cardSeparator));
        add(new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
                add(new TransparentPanel() {
                    {
                        add(packageLabel, BorderLayout.WEST);
                        add(hitsLabel, BorderLayout.EAST);
                    }
                }, BorderLayout.NORTH);
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(new TransparentPanel() {
                    {
                        add(groupInfoPanel, BorderLayout.SOUTH);
                        add(new TransparentPanel() {
                            {
                                setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                                setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                                add(titleLabel);
                            }
                        }, BorderLayout.NORTH);
                        add(new TransparentPanel() {
                            {
                                setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
                                add(exceptionMessageLabel, BorderLayout.CENTER);
                            }
                        }, BorderLayout.CENTER);
                    }
                }, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);

        setPreferredSize(new Dimension(400, Math.min(getPreferredSize().height, 250)));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.min(getPreferredSize().height, 250)));

    }

    @Override
    SearchGroup getSearchGroup() {
        return searchGroup;
    }

    // TODO separate TitleLabel by functionality? (i.e. one for history tab, that opens a search tab, and an other for search tab, that opens the browser.
    final class TitleLabel extends JLabel {
        {
            setText(exceptionType.className);
            setForeground(Colors.samebugOrange);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            attributes.put(TextAttribute.SIZE, 16);
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            setFont(getFont().deriveFont(attributes));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    actions.onClickTitle(searchGroup);
                }
            });
            setToolTipText(actions.getTitleMouseOverText(searchGroup));
        }
    }

    final class HitsLabel extends JLabel {
        static final int LIMIT = 100;

        {
            if (searchGroup.getNumberOfHits() > LIMIT) {
                setText(String.format("%d+ hits", LIMIT));
            } else {
                setText(String.format("%d hits", searchGroup.getNumberOfHits()));
            }
        }

        @Override
        public Color getForeground() {
            return ColorUtil.unemphasizedText();
        }
    }

    final class PackageLabel extends JLabel {
        {
            if (exceptionType.packageName == null) {
                setText(String.format("%s", SamebugBundle.message("samebug.exception.noPackage")));
            } else {
                setText(String.format("%s", exceptionType.packageName));
            }
        }

        @Override
        public Color getForeground() {
            return ColorUtil.unemphasizedText();
        }
    }
}
