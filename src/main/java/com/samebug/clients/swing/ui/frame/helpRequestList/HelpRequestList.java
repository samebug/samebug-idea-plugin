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
package com.samebug.clients.swing.ui.frame.helpRequestList;

import com.samebug.clients.common.tracking.Funnels;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestList;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.CenteredMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.EmphasizedPanel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.base.scrollPane.SamebugScrollPane;
import com.samebug.clients.swing.ui.component.helpRequest.HelpRequestPreview;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class HelpRequestList extends SamebugPanel implements IHelpRequestList {
    private final SamebugScrollPane scrollPane;
    private final ContentPanel contentPanel;
    private final List<HelpRequestPreview> previews;

    public HelpRequestList(Model model) {
        previews = new ArrayList<HelpRequestPreview>();
        for (int i = 0; i < model.requestPreviews.size(); i++) {
            HelpRequestPreview.Model m = model.requestPreviews.get(i);
            HelpRequestPreview hit = new HelpRequestPreview(m);
            DataService.putData(hit, TrackingKeys.WriteTipTransaction, Funnels.newTransactionId());
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
        ContentPanel() {
            if (previews.isEmpty()) {
                final EmptyListPanel emptyPanel = new EmptyListPanel();
                setLayout(new MigLayout("fillx", "20px[fill]0", "0[]0"));
                add(emptyPanel);
            } else {
                final ListPanel listPanel = new ListPanel();
                setLayout(new MigLayout("fillx", "20px[]0", "0[]20px[]20px"));
                add(listPanel, "cell 0 0, growx");
            }
        }
    }

    private final class ListPanel extends TransparentPanel {
        ListPanel() {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            // webHits is required to be initialized here (the hit views are actually added to the list)
            for (int i = 0; i < previews.size(); i++) {
                if (i != 0) add(Box.createRigidArea(new Dimension(0, 20)));
                HelpRequestPreview hit = previews.get(i);
                add(hit);
            }
        }
    }

    private final class EmptyListPanel extends EmphasizedPanel {
        {
            final SamebugLabel emptylabel = new SamebugLabel(MessageService.message("samebug.frame.helpRequestList.emptyList"));
            final LinkLabel openLabel = new LinkLabel(MessageService.message("samebug.frame.helpRequestList.openLabel"));
            final CenteredMultilineLabel description = new CenteredMultilineLabel();
            description.setText(MessageService.message("samebug.frame.helpRequestList.description"));

            openLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    removeAll();
                    setLayout(new MigLayout("fillx", "40px[300px]40px", "40px[]27px[]40px"));
                    add(emptylabel, "cell 0 0, al center");
                    add(description, "cell 0 1, growx, wmin 0");
                    revalidate();
                    repaint();
                }
            });

            setLayout(new MigLayout("fillx", "40px[300px]40px", "40px[]5px[]40px"));
            add(emptylabel, "cell 0 0, al center");
            add(openLabel, "cell 0 1, al center");

        }
    }

    private Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
