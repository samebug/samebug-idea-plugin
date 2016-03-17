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
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.ui.components.BreadcrumbBar;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

/**
 * Created by poroszd on 3/3/16.
 */
public class SearchGroupCardView {
    static final PrettyTime pretty = new PrettyTime(Locale.US);

    final Project project;
    final GroupedExceptionSearch searchGroup;
    boolean hover;

    public JPanel controlPanel;
    public JPanel paddingPanel;
    public JPanel infoBar;
    public JPanel breadcrumbPanel;
    public JPanel contentPanel;
    public TitleLabel titleLabel;
    public LastTimeLabel lastTimeLabel;
    public FirstTimeLabel firstTimeLabel;
    public HitsLabel hitsLabel;
    public MessageLabel messageLabel;


    public SearchGroupCardView(Project project, GroupedExceptionSearch searchGroup) {
        this.project = project;
        this.searchGroup = searchGroup;

        GridBagConstraints gbc;
        controlPanel = new JPanel();
        paddingPanel = new JPanel();
        infoBar = new JPanel();
        lastTimeLabel = new LastTimeLabel();
        hitsLabel = new HitsLabel();
        firstTimeLabel = new FirstTimeLabel();
        contentPanel = new JPanel();
        titleLabel = new TitleLabel();
        messageLabel = new MessageLabel();
        breadcrumbPanel = new BreadcrumbBar(searchGroup.lastSearch.componentStack);

        controlPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.setMaximumSize(new Dimension(2147483647, 150));
        controlPanel.setPreferredSize(new Dimension(400, 150));
        controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.black), null));
        controlPanel.add(paddingPanel, BorderLayout.CENTER);

        paddingPanel.setLayout(new BorderLayout(0, 0));
        paddingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10), null));
        paddingPanel.add(infoBar, BorderLayout.NORTH);
        paddingPanel.add(contentPanel, BorderLayout.CENTER);
        paddingPanel.add(breadcrumbPanel, BorderLayout.SOUTH);

        infoBar.setLayout(new GridBagLayout());
        infoBar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0), null));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        infoBar.add(lastTimeLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.EAST;
        infoBar.add(firstTimeLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        infoBar.add(hitsLabel, gbc);

        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        hitsLabel.setFont(new Font(hitsLabel.getFont().getName(), Font.BOLD, hitsLabel.getFont().getSize()));
        messageLabel.setFont(UIManager.getFont("TextArea.font"));
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
            return String.format("<html><b><a href=\"%s\">%s</a></b></html>",
                    searchGroup.lastSearch.searchUrl, searchGroup.lastSearch.exception.typeName);
        }
    }

    public class FirstTimeLabel extends JLabel {
        @Override
        public String getText() {
            return String.format("first %s", pretty.format(searchGroup.firstSeenSimilar));
        }

        @Override
        public int getHorizontalAlignment() {
            return SwingConstants.RIGHT;
        }

        @Override
        public int getHorizontalTextPosition() {
            return SwingConstants.RIGHT;
        }
    }

    public class LastTimeLabel extends JLabel {
        @Override
        public String getText() {
            return String.format("%s", pretty.format(searchGroup.lastSeenSimilar));
        }

        @Override
        public int getHorizontalAlignment() {
            return SwingConstants.LEFT;
        }

        @Override
        public int getHorizontalTextPosition() {
            return SwingConstants.LEFT;
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
            return SwingConstants.CENTER;
        }

        @Override
        public int getHorizontalTextPosition() {
            return SwingConstants.CENTER;
        }
    }

    public class MessageLabel extends JLabel {
        @Override
        public String getText() {
            String message = searchGroup.lastSearch.exception.message;
            if (message == null) {
                return String.format("<html><i>No message provided</i></html>");
            } else {
                return String.format("<html>%s</html>", message);
            }
        }

        @Override
        public int getVerticalAlignment() {
            return SwingConstants.TOP;
        }
    }
}
