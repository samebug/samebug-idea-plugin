package com.samebug.clients.swing.ui.frame.tipRequest;

import com.samebug.clients.common.ui.frame.tipRequest.ITipRequestTab;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.base.scrollPane.SamebugScrollPane;
import com.samebug.clients.swing.ui.component.hit.TipHit;
import com.samebug.clients.swing.ui.component.tipRequest.TipRequest;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public final class TipRequestTab extends TransparentPanel implements ITipRequestTab {
    private final JScrollPane scrollPane;
    private final SamebugPanel contentPanel;
    private final TipRequest request;
    private final java.util.List<TipHit> tipHits;

    public TipRequestTab(Model model) {
        request = new TipRequest(model.tipRequest);
        tipHits = new ArrayList<TipHit>();
        for (int i = 0; i < model.tipHits.size(); i++) {
            TipHit.Model m = model.tipHits.get(i);
            TipHit hit = new TipHit(m);
            tipHits.add(hit);
        }

        contentPanel = new ContentPanel();
        scrollPane = new SamebugScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(contentPanel);

        setLayout(new BorderLayout());
        add(scrollPane);
    }
    private final class ContentPanel extends SamebugPanel {
        {
            final ListPanel listPanel = new ListPanel();
            // TODO when empty
            final SamebugLabel tipListLabel = new SamebugLabel("Other tips", FontService.demi(16));

            setLayout(new MigLayout("fillx", "20[fill]0", "0[]20[]20[]20"));

            add(request, "cell 0 0");
            add(tipListLabel, "cell 0 1");
            add(listPanel, "cell 0 2");
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
