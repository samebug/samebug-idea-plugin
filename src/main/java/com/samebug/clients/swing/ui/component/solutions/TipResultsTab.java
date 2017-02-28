package com.samebug.clients.swing.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.solutions.ITipResultsTab;
import com.samebug.clients.swing.ui.SamebugBundle;
import com.samebug.clients.swing.ui.component.util.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import com.samebug.clients.swing.ui.component.util.scrollPane.SamebugScrollPane;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class TipResultsTab extends TransparentPanel implements ITipResultsTab {
    private final Model model;
    private final IHelpOthersCTA.Model ctaModel;
    private final MessageBus messageBus;

    private final JScrollPane scrollPane;
    private final SamebugPanel contentPanel;
    private final List<TipHit> tipHits;

    public TipResultsTab(MessageBus messageBus, Model model, IHelpOthersCTA.Model ctaModel) {
        this.model = new Model(model);
        this.ctaModel = new IHelpOthersCTA.Model(ctaModel);
        this.messageBus = messageBus;

        tipHits = new ArrayList<TipHit>();
        for (int i = 0; i < model.tipHits.size(); i++) {
            TipHit.Model m = model.tipHits.get(i);
            TipHit hit = new TipHit(messageBus, m);
            tipHits.add(hit);
        }
        if (model.getTipsSize() == 0) {
            contentPanel = new EmptyContentPanel();
        } else {
            contentPanel = new ContentPanel();
        }
        scrollPane = new SamebugScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(contentPanel);

        setLayout(new BorderLayout());
        add(scrollPane);
    }


    private final class EmptyContentPanel extends SamebugPanel {
        {
            final LargeWriteTipCTA cta = new LargeWriteTipCTA(messageBus, ctaModel) {
                {
                    label.setText(SamebugBundle.message("samebug.component.cta.writeTip.noTipHits.label", model.usersWaitingHelp));
                }
            };
            setLayout(new MigLayout("fillx", "20[fill]0", "0[]20"));
            add(cta);
        }
    }

    private final class ContentPanel extends SamebugPanel {
        {
            final ListPanel listPanel = new ListPanel();
            final SmallWriteTipCTA writeTip = new SmallWriteTipCTA(messageBus, ctaModel);
            final BugmateList bugmateList = new BugmateList(messageBus, model.bugmateList);

            setLayout(new MigLayout("fillx", "20[fill]0", "0[]20[]20[]20"));

            add(listPanel, "cell 0 0");
            add(writeTip, "cell 0 1");
            add(bugmateList, "cell 0 2");
        }
    }

    private final class ListPanel extends SamebugPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            // tipHits is required to be initialized here (the hit views are actually added to the list)
            for (int i = 0; i < tipHits.size(); i++) {
                if (i == 0) add(Box.createRigidArea(new Dimension(0, 10)));
                else add(Box.createRigidArea(new Dimension(0, 20)));
                TipHit hit = tipHits.get(i);
                add(hit);
            }
        }
    }
}
