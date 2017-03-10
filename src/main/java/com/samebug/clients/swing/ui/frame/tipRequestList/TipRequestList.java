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
package com.samebug.clients.swing.ui.frame.tipRequestList;

import com.samebug.clients.common.ui.frame.tipRequestList.ITipRequestList;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.base.scrollPane.SamebugScrollPane;
import com.samebug.clients.swing.ui.component.tipRequest.TipRequestPreview;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class TipRequestList extends SamebugPanel implements ITipRequestList {
    private final SamebugScrollPane scrollPane;
    private final ContentPanel contentPanel;
    private final List<TipRequestPreview> previews;

    public TipRequestList(Model model) {
        previews = new ArrayList<TipRequestPreview>();
        for (int i = 0; i < model.requestPreviews.size(); i++) {
            TipRequestPreview.Model m = model.requestPreviews.get(i);
            TipRequestPreview hit = new TipRequestPreview(m);
            previews.add(hit);
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
        public ContentPanel() {
            final ListPanel listPanel = new ListPanel();
            final MoreButton more = new MoreButton();

            setLayout(new MigLayout("fillx", "20[]0", "0[]20[]20"));

            add(listPanel, "cell 0 0, growx");
            add(more, "cell 0 1, al center");
        }
    }

    private final class ListPanel extends TransparentPanel {
        public ListPanel() {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            // webHits is required to be initialized here (the hit views are actually added to the list)
            for (int i = 0; i < previews.size(); i++) {
                if (i != 0) add(Box.createRigidArea(new Dimension(0, 20)));
                TipRequestPreview hit = previews.get(i);
                add(hit);
            }
        }
    }

    private final class MoreButton extends SamebugButton {
        {
            setText(MessageService.message("samebug.frame.tipRequestList.more"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().moreClicked();
                }
            });
        }
    }

    private Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
