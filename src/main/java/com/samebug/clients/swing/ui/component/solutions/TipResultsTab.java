/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.swing.ui.component.solutions;

import com.samebug.clients.common.ui.component.solutions.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.solutions.ITipResultsTab;
import com.samebug.clients.swing.ui.component.solutions.writeTip.WriteTip;
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

    private final JScrollPane scrollPane;
    private final SamebugPanel contentPanel;
    private final List<TipHit> tipHits;

    public TipResultsTab(Model model, IHelpOthersCTA.Model ctaModel) {
        this.model = new Model(model);
        this.ctaModel = new IHelpOthersCTA.Model(ctaModel);

        tipHits = new ArrayList<TipHit>();
        for (int i = 0; i < model.tipHits.size(); i++) {
            TipHit.Model m = model.tipHits.get(i);
            TipHit hit = new TipHit(m);
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
            final WriteTip cta = new WriteTip(ctaModel, WriteTip.CTA_TYPE.LARGE_FOR_TIP_HITS);
            setLayout(new MigLayout("fillx", "20[fill]0", "0[]20"));
            add(cta);
        }
    }

    private final class ContentPanel extends SamebugPanel {
        {
            final ListPanel listPanel = new ListPanel();
            final WriteTip writeTip = new WriteTip(ctaModel, WriteTip.CTA_TYPE.SMALL);
            final BugmateList bugmateList = new BugmateList(model.bugmateList);

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
