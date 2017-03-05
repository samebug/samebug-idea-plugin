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

import com.samebug.clients.common.ui.component.solutions.IBugmateList;
import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import com.samebug.clients.swing.ui.global.FontService;
import com.samebug.clients.swing.ui.global.ListenerService;
import com.samebug.clients.swing.ui.global.MessageService;
import net.miginfocom.swing.MigLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class BugmateList extends TransparentPanel implements IBugmateList {
    private final Model model;

    public BugmateList(Model model) {
        this.model = new Model(model);

        final SubheaderLabel subheader = new SubheaderLabel();
        final BugmateGrid bugmateGrid = new BugmateGrid();
        final MoreLabel more = new MoreLabel();
        final AskButton askButton = new AskButton();

        setLayout(new MigLayout("fillx", "0[]0", "0[]25[]25[]10[]0"));

        add(subheader, "cell 0 0");
        add(bugmateGrid, "cell 0 1, growx");
        add(more, "cell 0 2, align center");
        add(askButton, "cell 0 3, align center");
    }

    private final class BugmateGrid extends TransparentPanel {
        private final List<BugmateHit> bugmateHits;

        {
            bugmateHits = new ArrayList<BugmateHit>(model.bugmateHits.size());
            for (int i = 0; i < model.bugmateHits.size(); ++i) {
                BugmateHit hit = new BugmateHit(model.bugmateHits.get(i));
                bugmateHits.add(hit);
            }

            // TODO generalize it if necessary, for 4 items it's fine
            if (bugmateHits.size() <= 2) {
                setLayout(new MigLayout("fillx", "0[]20:push[]0", "0[]0"));
            } else {
                setLayout(new MigLayout("fillx", "0[]20:push[]0", "0[]20[]0"));
            }

            for (int i = 0; i < bugmateHits.size(); ++i) {
                BugmateHit hit = bugmateHits.get(i);
                if (i % 2 == 0) add(hit, "");
                else if (i == bugmateHits.size() - 1) add(hit, "");
                else add(hit, "wrap");
            }
        }
    }

    private final class SubheaderLabel extends SamebugLabel {
        {
            setText(MessageService.message("samebug.component.bugmate.list.title"));
            setFont(FontService.demi(16));
        }
    }

    private final class MoreLabel extends SamebugLabel {
        {
            setText(MessageService.message("samebug.component.bugmate.list.more", model.numberOfOtherBugmates));
            setFont(FontService.regular(14));
        }
    }

    private final class AskButton extends SamebugButton {
        {
            setText(MessageService.message("samebug.component.bugmate.list.ask"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().askBugmates(BugmateList.this);
                }
            });
        }
    }

    private Listener getListener() {
        return ListenerService.getListener(this, IBugmateList.Listener.class);
    }
}
