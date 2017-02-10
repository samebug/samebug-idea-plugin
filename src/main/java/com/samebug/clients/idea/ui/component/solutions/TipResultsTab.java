package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.ui.ColorUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class TipResultsTab extends JPanel {
    private final Model model;
    private final MessageBus messageBus;

    private final JScrollPane scrollPane;
    private final ContentPanel contentPanel;
    private final List<TipHit> tipHits;

    public TipResultsTab(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        tipHits = new ArrayList<TipHit>();
        for (int i = 0; i < model.tipHits.size(); i++) {
            TipHit.Model m = model.tipHits.get(i);
            TipHit hit = new TipHit(messageBus, m);
            tipHits.add(hit);
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
            final WriteTipCTA writeTip = new WriteTipCTA(messageBus, model.writeTipHint);

            setBackground(ColorUtil.background());
            setLayout(new MigLayout("fillx", "0[fill]0", "0[]20[]0"));

            add(listPanel, "cell 0 0");
            add(writeTip, "cell 0 1");
        }
    }

    private final class ListPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setBackground(ColorUtil.background());

            // tipHits is required to be initialized here (the hit views are actually added to the list)
            for (int i = 0; i < tipHits.size(); i++) {
                if (i == 0) add(Box.createRigidArea(new Dimension(0, 30)));
                else add(Box.createRigidArea(new Dimension(0, 20)));
                TipHit hit = tipHits.get(i);
                add(hit);
            }
        }
    }


    public static final class Model {
        private final List<TipHit.Model> tipHits;
        private final WriteTipCTA.Model writeTipHint;

        public Model(Model rhs) {
            this(rhs.tipHits, rhs.writeTipHint);
        }

        public Model(List<TipHit.Model> tipHits, WriteTipCTA.Model writeTipHint) {
            this.tipHits = tipHits;
            this.writeTipHint = writeTipHint;
        }

        public int getTipsSize() {
            return tipHits.size();
        }
    }
}
