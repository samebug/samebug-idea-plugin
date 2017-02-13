package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.component.util.SamebugButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class WebResultsTab extends JPanel {
    private final Model model;
    private final MessageBus messageBus;

    private final JScrollPane scrollPane;
    private final ContentPanel contentPanel;
    private final List<WebHit> webHits;

    public WebResultsTab(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        webHits = new ArrayList<WebHit>();
        for (int i = 0; i < model.webHits.size(); i++) {
            WebHit.Model m = model.webHits.get(i);
            WebHit hit = new WebHit(messageBus, m);
            webHits.add(hit);
        }

        contentPanel = new ContentPanel();
        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(contentPanel);
        scrollPane.setBorder(null);

        setLayout(new BorderLayout());
        add(scrollPane);
    }


    private final class ContentPanel extends JPanel {
        {
            final ListPanel listPanel = new ListPanel();
            final MoreButton more = new MoreButton();

            setBackground(ColorUtil.background());
            setLayout(new MigLayout("fillx", "20[]20", "0[]10[]20"));

            add(listPanel, "cell 0 0, growx");
            add(more, "cell 0 1, al center");
        }
    }


    private final class ListPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setOpaque(false);

            // webHits is required to be initialized here (the hit views are actually added to the list)
            for (int i = 0; i < webHits.size(); i++) {
                if (i != 0) add(new Separator());
                WebHit hit = webHits.get(i);
                add(hit);
            }
        }
    }

    private final static class Separator extends JPanel {
        {
            setPreferredSize(new Dimension(0, 20 + 1 + 20));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 20 + 1 + 20));
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = DrawUtil.init(g);
            g2.setColor(ColorUtil.background());
            g2.fillRect(0,0, getWidth(), getHeight());
            g2.setColor(ColorUtil.separator());
            g2.drawLine(0,21, getWidth(), 21);
        }
    }

    private final class MoreButton extends SamebugButton {
        {
            setText(SamebugBundle.message("samebug.component.webResults.more"));
        }
    }

    public static final class Model {
        private final List<WebHit.Model> webHits;

        public Model(Model rhs) {
            this(rhs.webHits);
        }

        public Model(List<WebHit.Model> webHits) {
            this.webHits = webHits;
        }

        public int getHitsSize() {
            return webHits.size();
        }
    }
}
