package com.samebug.clients.idea.ui.views;

import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.HtmlUtil;
import com.samebug.clients.search.api.entities.ComponentStack;
import com.samebug.clients.search.api.entities.Exception;
import com.samebug.clients.search.api.entities.ExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * Created by poroszd on 3/3/16.
 */
public class SearchGroupCardView {
    final private Project project;
    public JPanel controlPanel;
    public JPanel leftPanel;
    public JPanel breadcrumbPanel;
    public JPanel contentPanel;
    public JEditorPane breadcrumbBar;
    public JEditorPane titleLabel;
    public JLabel timeLabel;
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


        timeLabel.setText(searchGroup.lastSeenSimilar.toString());

        breadcrumbBar.setText(HtmlUtil.breadcrumbs(stacks));
    }

    public SearchGroupCardView(Project project) {
        this.project = project;
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.setPreferredSize(new Dimension(400, 150));
        controlPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setMinimumSize(new Dimension(80, 35));
        leftPanel.setPreferredSize(new Dimension(80, 35));
        controlPanel.add(leftPanel, BorderLayout.WEST);
        leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        hitsLabel = new JLabel();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(hitsLabel, gbc);
        timeLabel = new JLabel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(timeLabel, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        leftPanel.add(spacer1, gbc);
        breadcrumbPanel = new JPanel();
        breadcrumbPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        controlPanel.add(breadcrumbPanel, BorderLayout.SOUTH);
        breadcrumbPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0), null));
        breadcrumbBar = new JEditorPane();
        breadcrumbBar.setContentType("text/html");
        breadcrumbBar.setEditable(false);
        breadcrumbBar.setOpaque(false);
        breadcrumbPanel.add(breadcrumbBar);
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.add(contentPanel, BorderLayout.CENTER);
        contentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0), null));
        titleLabel = new JEditorPane();
        titleLabel.setContentType("text/html");
        titleLabel.setEditable(false);
        titleLabel.setOpaque(false);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        messageLabel = new JLabel();
        messageLabel.setFont(UIManager.getFont("TextArea.font"));
        messageLabel.setVerticalAlignment(1);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

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
