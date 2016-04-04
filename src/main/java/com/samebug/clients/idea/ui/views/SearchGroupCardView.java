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

import com.intellij.ui.HighlightableComponent;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.components.BreadcrumbBar;
import com.samebug.clients.idea.ui.components.ExceptionMessageLabel;
import com.samebug.clients.idea.ui.components.LinkLabel;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by poroszd on 3/3/16.
 */
public class SearchGroupCardView {
    static final PrettyTime pretty = new PrettyTime(Locale.US);

    final GroupedExceptionSearch searchGroup;
    final String packageName;
    final String className;

    public JPanel controlPanel;
    public JPanel paddingPanel;
    public JPanel contentPanel;
    public JPanel breadcrumbPanel;
    public JPanel titlePanel;
    public JPanel messagePanel;
    public TitleLabel titleLabel;
    public TopBar topBar;
    public GroupInfoPanel groupInfoPanel;

    public SearchGroupCardView(GroupedExceptionSearch searchGroup) {
        this.searchGroup = searchGroup;
        int dotIndex = searchGroup.lastSearch.exception.typeName.lastIndexOf('.');
        if (dotIndex < 0) {
            this.packageName = null;
            this.className = searchGroup.lastSearch.exception.typeName;
        } else {
            this.packageName = searchGroup.lastSearch.exception.typeName.substring(0, dotIndex);
            this.className = searchGroup.lastSearch.exception.typeName.substring(dotIndex + 1);
        }

        GridBagConstraints gbc;
        controlPanel = new ControlPanel();
        paddingPanel = new JPanel();
        topBar = new TopBar();
        groupInfoPanel = new GroupInfoPanel();
        contentPanel = new JPanel();
        titlePanel = new JPanel();
        titleLabel = new TitleLabel();
        messagePanel = new JPanel();
        breadcrumbPanel = new BreadcrumbBar(searchGroup.lastSearch.componentStack);

        controlPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        controlPanel.add(paddingPanel, BorderLayout.CENTER);

        paddingPanel.setLayout(new BorderLayout(0, 0));
        paddingPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
        paddingPanel.add(topBar, BorderLayout.NORTH);
        paddingPanel.add(contentPanel, BorderLayout.CENTER);
        paddingPanel.add(breadcrumbPanel, BorderLayout.SOUTH);

        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(messagePanel, BorderLayout.CENTER);
        contentPanel.add(groupInfoPanel, BorderLayout.SOUTH);

        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        titlePanel.add(titleLabel);

        messagePanel.setLayout(new BorderLayout(0, 0));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        messagePanel.add(new ExceptionMessageLabel(searchGroup.lastSearch.exception.message));

        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        attributes.put(TextAttribute.SIZE, 16);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        titleLabel.setFont(titleLabel.getFont().deriveFont(attributes));
    }

    public class ControlPanel extends JPanel {
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

    public class TitleLabel extends JLabel {
        @Override
        public String getText() {
            return String.format("%s", className);
        }

        @Override
        public Color getForeground() {
            return Colors.samebugOrange;
        }
    }

    public class HitsLabel extends JLabel {
        static final int LIMIT = 100;

        @Override
        public String getText() {
            if (searchGroup.numberOfSolutions > LIMIT) {
                return String.format("%d+ hits", LIMIT);
            } else {
                return String.format("%d hits", searchGroup.numberOfSolutions);
            }
        }

        @Override
        public Color getForeground() {
            return ColorUtil.unemphasizedText();
        }
    }

    public class PackageLabel extends JLabel {
        @Override
        public String getText() {
            if (packageName == null) {
                return String.format("%s", SamebugBundle.message("samebug.exception.noPackage"));
            } else {
                return String.format("%s", packageName);
            }
        }

        @Override
        public Color getForeground() {
            return ColorUtil.unemphasizedText();
        }
    }

    public class TopBar extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder());
            add(new PackageLabel(), BorderLayout.WEST);
            add(new HitsLabel(), BorderLayout.EAST);
        }
    }

    public class GroupInfoPanel extends JPanel {
        {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            add(new JLabel() {
                {
                    String text;
                    if (searchGroup.numberOfSimilars == 1) {
                        text = String.format("%s", pretty.format(searchGroup.lastSeenSimilar));
                    } else {
                        text = String.format("%s | %d times, first %s", pretty.format(searchGroup.lastSeenSimilar), searchGroup.numberOfSimilars, pretty.format(searchGroup.firstSeenSimilar));
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

}
