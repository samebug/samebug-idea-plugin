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
package com.samebug.clients.swing.ui.component.bugmate;

import com.samebug.clients.common.ui.component.bugmate.IBugmateHit;
import com.samebug.clients.common.ui.component.bugmate.IBugmateList;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import java.util.ArrayList;
import java.util.List;

public final class BugmateList extends TransparentPanel implements IBugmateList {
    final Model model;

    public BugmateList(Model model) {
        this.model = new Model(model);

        final SubheaderLabel subheader = new SubheaderLabel();
        final BugmateGrid bugmateGrid = new BugmateGrid(model.bugmateHits);
        if (model.numberOfOtherBugmates == 0) {
            setLayout(new MigLayout("fillx", "0[]0", "0[]25px[]0"));

            add(subheader, "cell 0 0");
            add(bugmateGrid, "cell 0 1, growx");
        } else {
            final MoreLabel more = new MoreLabel(model.numberOfOtherBugmates);
            setLayout(new MigLayout("fillx", "0[]0", "0[]25px[]25px[]0"));

            add(subheader, "cell 0 0");
            add(bugmateGrid, "cell 0 1, growx");
            add(more, "cell 0 2, align center");
        }
    }

    private final class BugmateGrid extends TransparentPanel {
        private final List<BugmateHit> bugmateHits;

        private BugmateGrid(List<IBugmateHit.Model> bugmateHitModels) {
            bugmateHits = new ArrayList<BugmateHit>(bugmateHitModels.size());
            for (IBugmateHit.Model bugmateHitModel : bugmateHitModels) {
                BugmateHit hit = new BugmateHit(bugmateHitModel);
                bugmateHits.add(hit);
            }

            // TODO generalize it if necessary, for 4 items it's fine
            if (bugmateHits.size() <= 2) {
                setLayout(new MigLayout("fillx", "0[]20px:push[]0", "0[]0"));
            } else {
                setLayout(new MigLayout("fillx", "0[]20px:push[]0", "0[]20px[]0"));
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
        private MoreLabel(int numberOfOtherBugmates) {
            assert numberOfOtherBugmates != 0 : "Do not display this label if there are no other bugmates";
            setText(MessageService.message("samebug.component.bugmate.list.more", numberOfOtherBugmates));
            setFont(FontService.regular(14));
        }
    }
}
