package com.samebug.clients.idea.ui.component.experimental;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WebResultsTab extends JPanel {
    final JScrollPane scrollPane;
    final JPanel contentPanel;

    public WebResultsTab() {
        scrollPane = new JScrollPane();
        contentPanel = new ContentPanel();

        scrollPane.setViewportView(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.setBorder(null);

        setLayout(new BorderLayout());
        add(scrollPane);
    }
}

final class ContentPanel extends JPanel {
    public ContentPanel() {
        JPanel l = new JPanel() {
            {
                List<WebHit> hits = new ArrayList<WebHit>();
                hits.add(new WebHit());
                hits.add(new WebHit());
                hits.add(new WebHit());
                hits.add(new WebHit());

                setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

                for (int i = 0; i < hits.size(); i++) {
                    if (i != 0) add(Box.createRigidArea(new Dimension(0, 1)));
                    add(hits.get(i));
                }

                setBackground(Constants.SeparatorColor);
            }
        };

        JButton more = new JButton("more");

        setBackground(Color.white);
        setLayout(new MigLayout("fillx",
                "0[fill]0",
                "0[]10[]0"));
        add(l, "cell 0 0");
        add(more, "cell 0 1");

    }
}
