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

import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.HtmlUtil;
import com.samebug.clients.search.api.entities.ComponentStack;
import com.samebug.clients.search.api.entities.Exception;
import com.samebug.clients.search.api.entities.ExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.Locale;

/**
 * Created by poroszd on 3/3/16.
 */
public class SearchGroupCardView {
    final private Project project;
    public JPanel controlPanel;
    public JPanel paddingPanel;
    public JPanel infoBar;
    public JPanel breadcrumbPanel;
    public JPanel contentPanel;
    public JEditorPane breadcrumbBar;
    public JEditorPane titleLabel;
    public JLabel lastTimeLabel;
    public JLabel firstTimeLabel;
    public JLabel hitsLabel;
    public JLabel messageLabel;

    public void setContent(GroupedExceptionSearch searchGroup) {
        ExceptionSearch search = searchGroup.lastSearch;
        Exception exception = search.exception;
        java.util.List<ComponentStack> stacks = search.componentStack;

        titleLabel.setText(String.format("<html><b><a href=\"%s\">%s</a></b></html>", search.searchUrl, exception.typeName));
        if (exception.message == null) {
            messageLabel.setText(String.format("<html><i>%s</i></html>", SamebugBundle.message("samebug.exception.noMessage")));
        } else {
            messageLabel.setText(HtmlUtil.html(exception.message));
        }

        final int LIMIT = 100;
        if (searchGroup.numberOfSolutions > LIMIT) {
            hitsLabel.setText(String.format("%d+ hits", LIMIT));
        } else {
            hitsLabel.setText(String.format("%d hits", searchGroup.numberOfSolutions));
        }


        PrettyTime pretty = new PrettyTime(Locale.US);
        lastTimeLabel.setText(String.format("%s", pretty.format(searchGroup.lastSeenSimilar)));
        firstTimeLabel.setText(String.format("first %s", pretty.format(searchGroup.firstSeenSimilar)));

        breadcrumbBar.setText(HtmlUtil.breadcrumbs(stacks));
    }

    public SearchGroupCardView(Project project) {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.setMaximumSize(new Dimension(2147483647, 150));
        controlPanel.setPreferredSize(new Dimension(400, 150));
        paddingPanel = new JPanel();
        paddingPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.add(paddingPanel, BorderLayout.CENTER);
        paddingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10), null));
        infoBar = new JPanel();
        infoBar.setLayout(new GridBagLayout());
        paddingPanel.add(infoBar, BorderLayout.NORTH);
        infoBar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0), null));
        lastTimeLabel = new JLabel();
        lastTimeLabel.setHorizontalAlignment(2);
        lastTimeLabel.setHorizontalTextPosition(2);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        infoBar.add(lastTimeLabel, gbc);
        firstTimeLabel = new JLabel();
        firstTimeLabel.setHorizontalAlignment(4);
        firstTimeLabel.setHorizontalTextPosition(4);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.EAST;
        infoBar.add(firstTimeLabel, gbc);
        hitsLabel = new JLabel();
        hitsLabel.setFont(new Font(hitsLabel.getFont().getName(), Font.BOLD, hitsLabel.getFont().getSize()));
        hitsLabel.setHorizontalAlignment(0);
        hitsLabel.setHorizontalTextPosition(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        infoBar.add(hitsLabel, gbc);
        breadcrumbPanel = new JPanel();
        breadcrumbPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        paddingPanel.add(breadcrumbPanel, BorderLayout.SOUTH);
        breadcrumbPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null));
        breadcrumbBar = new JEditorPane();
        breadcrumbBar.setContentType("text/html");
        breadcrumbBar.setEditable(false);
        breadcrumbBar.setMargin(new Insets(0, 0, 0, 0));
        breadcrumbBar.setOpaque(false);
        breadcrumbPanel.add(breadcrumbBar);
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        paddingPanel.add(contentPanel, BorderLayout.CENTER);
        contentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null));
        titleLabel = new JEditorPane();
        titleLabel.setContentType("text/html");
        titleLabel.setEditable(false);
        titleLabel.setMargin(new Insets(1, 0, 0, 0));
        titleLabel.setOpaque(false);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        messageLabel = new JLabel();
        messageLabel.setEnabled(true);
        messageLabel.setFont(UIManager.getFont("TextArea.font"));
        messageLabel.setVerticalAlignment(1);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        this.project = project;
        controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.black), null));
        ((DefaultCaret) breadcrumbBar.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        ((DefaultCaret) titleLabel.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        HTMLEditorKit kit = new HTMLEditorKit();
        breadcrumbBar.setEditorKit(kit);
        breadcrumbBar.addHyperlinkListener(SamebugNotifications.basicHyperlinkListener(project, "searches-breadcrumb"));
        kit = new HTMLEditorKit();
        titleLabel.setEditorKit(kit);
        titleLabel.addHyperlinkListener(SamebugNotifications.basicHyperlinkListener(project, "searches-title"));
    }

}
