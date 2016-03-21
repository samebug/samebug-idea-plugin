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
package com.samebug.clients.idea.ui.views;

import com.intellij.ide.BrowserUtil;
import com.intellij.util.ui.UIUtil;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.components.BreadcrumbBar;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import org.apache.commons.lang.StringEscapeUtils;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public ControlPanel controlPanel;
    public JPanel paddingPanel;
    public JPanel infoBar;
    public JPanel contentPanel;
    public JPanel breadcrumbPanel;
    public JPanel titlePanel;
    public JPanel messagePanel;
    public PackageLabel packageLabel;
    public TitleLabel titleLabel;
    public TimeLabel timeLabel;
    public HitsLabel hitsLabel;
    public MessageLabel messageLabel;

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
        infoBar = new JPanel();
        timeLabel = new TimeLabel();
        hitsLabel = new HitsLabel();
        contentPanel = new JPanel();
        titlePanel = new JPanel();
        packageLabel = new PackageLabel();
        titleLabel = new TitleLabel();
        messagePanel = new JPanel();
        messageLabel = new MessageLabel();
        breadcrumbPanel = new BreadcrumbBar(searchGroup.lastSearch.componentStack);

        controlPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        controlPanel.add(paddingPanel, BorderLayout.CENTER);

        paddingPanel.setLayout(new BorderLayout(0, 0));
        paddingPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
        paddingPanel.add(infoBar, BorderLayout.NORTH);
        paddingPanel.add(contentPanel, BorderLayout.CENTER);
        paddingPanel.add(breadcrumbPanel, BorderLayout.SOUTH);

        infoBar.setLayout(new GridBagLayout());
        infoBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        infoBar.add(timeLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.EAST;
        infoBar.add(hitsLabel, gbc);

        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(messagePanel, BorderLayout.CENTER);

        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        titlePanel.add(packageLabel);
        titlePanel.add(titleLabel);

        messagePanel.setLayout(new BorderLayout(0, 0));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        messagePanel.add(messageLabel);

        messageLabel.setFont(UIManager.getFont("TextArea.font"));

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
            if (UIUtil.isUnderDarcula()) {
                return Colors.unemphasizedDarcula;
            } else {
                return Colors.unemphasized;
            }
        }
    }

    public class TitleLabel extends JLabel {
        public TitleLabel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BrowserUtil.browse(searchGroup.lastSearch.searchUrl);
                }
            });
        }

        @Override
        public String getText() {
            return String.format("%s", className);
        }

        @Override
        public Color getForeground() {
            return Colors.samebugOrange;
        }
    }

    public class TimeLabel extends JLabel {
        @Override
        public String getText() {
            return String.format("%s | %d times, first %s", pretty.format(searchGroup.lastSeenSimilar), searchGroup.numberOfSimilars, pretty.format(searchGroup.firstSeenSimilar));
        }

        @Override
        public int getHorizontalAlignment() {
            return SwingConstants.LEFT;
        }

        @Override
        public int getHorizontalTextPosition() {
            return SwingConstants.LEFT;
        }

        @Override
        public Color getForeground() {
            if (UIUtil.isUnderDarcula()) {
                return Colors.unemphasizedDarcula;
            } else {
                return Colors.unemphasized;
            }
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
        public int getHorizontalAlignment() {
            return SwingConstants.RIGHT;
        }

        @Override
        public int getHorizontalTextPosition() {
            return SwingConstants.RIGHT;
        }

        @Override
        public Color getForeground() {
            if (UIUtil.isUnderDarcula()) {
                return Colors.unemphasizedDarcula;
            } else {
                return Colors.unemphasized;
            }
        }
    }

    public class MessageLabel extends JLabel {
        private final String escapedText;
        public MessageLabel() {
            String message = searchGroup.lastSearch.exception.message;
            if (message == null) {
                escapedText = String.format("<html><i>No message provided</i></html>");
            } else {
                // Escape html, but keep line breaks
                String broken = StringEscapeUtils.escapeHtml(message).replaceAll("\\n", "<br>");
                escapedText = String.format("<html>%s</html>", broken);
            }
        }
        @Override
        public String getText() {
            return escapedText;
        }

        @Override
        public int getVerticalAlignment() {
            return SwingConstants.TOP;
        }
    }
}
