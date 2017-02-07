package com.samebug.clients.idea.ui.component.experimental;

import com.intellij.util.messages.MessageBus;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WebResultsTab extends JPanel {
    Model model;

    @NotNull
    JScrollPane scrollPane;
    @NotNull
    final ContentPanel contentPanel;
    @NotNull
    final List<WebHit> webHits;
    @NotNull
    final MessageBus messageBus;

    public WebResultsTab(MessageBus messageBus) {
        scrollPane = new JScrollPane();
        contentPanel = new ContentPanel();
        webHits = new ArrayList<WebHit>();
        this.messageBus = messageBus;

        setLayout(new BorderLayout());
    }

    public void update(Model model) {
        this.model = new Model(model);

        for (int i = 0; i < model.webhits.size(); i++) {
            WebHit.Model m = model.webhits.get(i);
            if (i != 0) add(Box.createRigidArea(new Dimension(0, 1)));
            webHits.add(new WebHit(messageBus, m));
        }
        // TODO not sure why I have to reconstruct the scrollpane. Simple revalidate() did not work
        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.setViewportView(contentPanel);
        scrollPane.setBorder(null);
        contentPanel.update(webHits);
        add(scrollPane);
    }

    public static final class Model {
        public List<WebHit.Model> webhits;

        public Model(Model rhs) {
            this(rhs.webhits);
        }

        public Model(List<WebHit.Model> webhits) {
            this.webhits = webhits;
        }
    }
}

final class ContentPanel extends JPanel {
    final ListPanel listPanel;
    final JButton more;

    {
        listPanel = new ListPanel();
        more = new JButton("more");

        setBackground(Color.white);
        setLayout(new MigLayout("fillx", "0[fill]0", "0[]10[]0"));

        add(listPanel, "cell 0 0");
        add(more, "cell 0 1");
    }

    public void update(List<WebHit> webHits) {
        listPanel.update(webHits);
    }


    private static final class ListPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setBackground(Constants.SeparatorColor);
        }

        public void update(List<WebHit> webHits) {
            removeAll();
            for (WebHit hit : webHits) {
                add(hit);
            }
        }

    }

}
