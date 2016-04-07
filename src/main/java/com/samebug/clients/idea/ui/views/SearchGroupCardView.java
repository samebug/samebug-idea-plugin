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

import com.samebug.clients.common.entities.ExceptionType;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.components.BreadcrumbBar;
import com.samebug.clients.idea.ui.components.ExceptionMessageLabel;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 3/3/16.
 */
public class SearchGroupCardView {
    final GroupedExceptionSearch searchGroup;
    final ExceptionType exceptionType;

    public JPanel controlPanel;

    public SearchGroupCardView(final GroupedExceptionSearch searchGroup, final ActionHandler actionHandler) {
        this.searchGroup = searchGroup;
        exceptionType = new ExceptionType(searchGroup.lastSearch.exception.typeName);

        final JLabel packageLabel = new PackageLabel();
        final JLabel hitsLabel = new HitsLabel();
        final JLabel titleLabel = new TitleLabel();
        final JLabel exceptionMessageLabel = new ExceptionMessageLabel(searchGroup.lastSearch.exception.message);
        final JPanel groupInfoPanel = new GroupInfoPanel();
        final JPanel breadcrumbPanel = new BreadcrumbBar(searchGroup.lastSearch.componentStack);

        controlPanel = new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout());
                        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.cardSeparator));
                        add(new JPanel() {
                            {
                                setLayout(new BorderLayout());
                                setBorder(BorderFactory.createEmptyBorder());
                                add(packageLabel, BorderLayout.WEST);
                                add(hitsLabel, BorderLayout.EAST);
                            }
                        }, BorderLayout.NORTH);
                        add(breadcrumbPanel, BorderLayout.SOUTH);
                        add(new JPanel() {
                            {
                                setLayout(new BorderLayout());
                                setBorder(BorderFactory.createEmptyBorder());
                                add(groupInfoPanel, BorderLayout.SOUTH);
                                add(new JPanel() {
                                    {
                                        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                                        setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                                        add(titleLabel);
                                    }
                                }, BorderLayout.NORTH);
                                add(new JPanel() {
                                    {
                                        setLayout(new BorderLayout());
                                        setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
                                        add(exceptionMessageLabel, BorderLayout.CENTER);
                                    }
                                }, BorderLayout.CENTER);
                            }
                        }, BorderLayout.CENTER);
                    }
                }, BorderLayout.CENTER);

                setPreferredSize(new Dimension(400, getPreferredSize().height));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.min(getPreferredSize().height, 250)));
            }
        };

        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                actionHandler.onTitleClick();
            }
        });
    }

    class TitleLabel extends JLabel {
        {
            setText(exceptionType.className);
            setForeground(Colors.samebugOrange);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            attributes.put(TextAttribute.SIZE, 16);
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            setFont(getFont().deriveFont(attributes));
        }
    }

    class HitsLabel extends JLabel {
        static final int LIMIT = 100;

        {
            if (searchGroup.numberOfSolutions > LIMIT) {
                setText(String.format("%d+ hits", LIMIT));
            } else {
                setText(String.format("%d hits", searchGroup.numberOfSolutions));
            }
        }

        @Override
        public Color getForeground() {
            return ColorUtil.unemphasizedText();
        }
    }

    class PackageLabel extends JLabel {
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

    class GroupInfoPanel extends JPanel {
        {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            add(new JLabel() {
                {
                    String text;
                    if (searchGroup.numberOfSimilars == 1) {
                        text = String.format("%s", TextUtil.prettyTime(searchGroup.lastSeenSimilar));
                    } else {
                        text = String.format("%s | %d times, first %s",
                                TextUtil.prettyTime(searchGroup.lastSeenSimilar), searchGroup.numberOfSimilars, TextUtil.prettyTime(searchGroup.firstSeenSimilar));
                    }

                    setText(text);
                }

                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasizedText();
                }
            });

        }
    }

    public interface ActionHandler {
        void onTitleClick();
    }
}
