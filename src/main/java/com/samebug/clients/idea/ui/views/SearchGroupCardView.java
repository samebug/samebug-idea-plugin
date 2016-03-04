package com.samebug.clients.idea.ui.views;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 3/3/16.
 */
public class SearchGroupCardView {
    public JPanel controlPanel;
    public JPanel leftPanel;
    public JPanel breadcrumbPanel;
    public JPanel contentPanel;
    public JEditorPane breadcrumbBar;
    public JLabel titleLabel;
    public JLabel timeLabel;
    public JLabel hitsLabel;

    public SearchGroupCardView() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setMinimumSize(new Dimension(80, 35));
        leftPanel.setPreferredSize(new Dimension(80, 35));
        controlPanel.add(leftPanel, BorderLayout.WEST);
        leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        hitsLabel = new JLabel();
        hitsLabel.setText("42 hits");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(hitsLabel, gbc);
        timeLabel = new JLabel();
        timeLabel.setText("3:12 PM");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(timeLabel, gbc);
        breadcrumbPanel = new JPanel();
        breadcrumbPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        controlPanel.add(breadcrumbPanel, BorderLayout.SOUTH);
        breadcrumbPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0), null));
        breadcrumbBar = new JEditorPane();
        breadcrumbBar.setContentType("text/html");
        breadcrumbBar.setEditable(false);
        breadcrumbBar.setText("<html>\n  <head>\n    \n  </head>\n  <body>\n    <span><font color=\"black\">de.appplant.cordova</font></span> &#8249; <a href=\"\\/crashdocs/android.app/ActivityThread/main/java.lang.RuntimeException?pt=true\\\"><font color=\"black\">Android \n    Platform</font> </a>&#8249; <a href=\"\\/crashdocs/java.lang.reflect/Method/invoke/java.lang.RuntimeException?pt=true\\\"><font color=\"red\">Java \n    RT</font> </a>&#8249; <a href=\"\\/crashdocs/com.android.internal.os/ZygoteInit/main/java.lang.RuntimeException?pt=true\\\"><font color=\"green\">Android</font> \n    </a>&#8249; <a href=\"\\/crashdocs/dalvik.system/NativeStart/main/java.lang.RuntimeException?pt=true\\\"><font color=\"red\">Android \n    Platform</font> </a>\n  </body>\n</html>\n");
        breadcrumbBar.setOpaque(false);
        breadcrumbPanel.add(breadcrumbBar);
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.add(contentPanel, BorderLayout.CENTER);
        contentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0), null));
        titleLabel = new JLabel();
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, titleLabel.getFont().getSize()));
        titleLabel.setForeground(new Color(-16776961));
        titleLabel.setText("<html><u>HttpError: org.apache.http.conn.HttpHostConnectException: Connect to localhost:9000 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused</u></html>");
        contentPanel.add(titleLabel, BorderLayout.CENTER);
    }

}
