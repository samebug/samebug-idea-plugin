/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.frame.helpRequest;

import com.samebug.clients.common.ui.component.helpRequest.IHelpRequest;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestTab;
import com.samebug.clients.swing.ui.base.animation.ControllableAnimation;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.base.scrollPane.SamebugScrollPane;
import com.samebug.clients.swing.ui.component.helpRequest.AnsweredHelpRequest;
import com.samebug.clients.swing.ui.component.helpRequest.NonAnsweredHelpRequest;
import com.samebug.clients.swing.ui.component.hit.NonMarkableTipHit;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public final class HelpRequestTab extends TransparentPanel implements IHelpRequestTab {
    private final JScrollPane scrollPane;
    private final SamebugPanel contentPanel;
    @NotNull
    private final NonAnsweredHelpRequest request;
    @Nullable
    private AnsweredHelpRequest response;
    private final java.util.List<NonMarkableTipHit> tipHits;

    public HelpRequestTab(Model model) {
        request = new NonAnsweredHelpRequest(model.helpRequest);
        tipHits = new ArrayList<NonMarkableTipHit>();
        for (int i = 0; i < model.tipHits.size(); i++) {
            NonMarkableTipHit.Model m = model.tipHits.get(i);
            NonMarkableTipHit hit = new NonMarkableTipHit(m);
            tipHits.add(hit);
        }

        contentPanel = new ContentPanel();
        scrollPane = new SamebugScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        setLayout(new BorderLayout());
        add(scrollPane);
    }

    public ControllableAnimation animatedAddResponse(@NotNull final ITipHit.Model newTipModel) {
        assert response == null : "This help request is already in answered state";

        IHelpRequest.Model helpRequestModel = request.getModel();
        response = new AnsweredHelpRequest(helpRequestModel, newTipModel);
        ControllableAnimation shrinkAwayHelpRequest = request.shrinkAway(response.getPreferredSize().height);
        ControllableAnimation fadeInResponse = response.fadeIn();
        fadeInResponse.runBeforeStart(new Runnable() {
            @Override
            public void run() {
                contentPanel.remove(0);
                contentPanel.add(response, 0);
                contentPanel.validate();
            }
        });

        return shrinkAwayHelpRequest.andThen(fadeInResponse);
    }

    private final class ContentPanel extends SamebugPanel {
        {
            if (tipHits.isEmpty()) {
                setLayout(new MigLayout("fillx", "20px[fill]0", "0[]20px"));
                add(request, "cell 0 0");
            } else {
                final ListPanel listPanel = new ListPanel();
                final SamebugLabel tipListLabel = new SamebugLabel(MessageService.message("samebug.component.helpRequest.answer.otherTips"), FontService.demi(16));

                setLayout(new MigLayout("fillx", "20px[fill]0", "0[]20px[]20px[]20px"));
                add(request, "cell 0 0");
                add(tipListLabel, "cell 0 1");
                add(listPanel, "cell 0 2");
            }
        }
    }

    private final class ListPanel extends SamebugPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            // tipHits is required to be initialized here (the hit views are actually added to the list)
            for (int i = 0; i < tipHits.size(); i++) {
                if (i == 0) add(Box.createRigidArea(new Dimension(0, 10)));
                else add(Box.createRigidArea(new Dimension(0, 20)));
                NonMarkableTipHit hit = tipHits.get(i);
                add(hit);
            }
        }
    }
}
