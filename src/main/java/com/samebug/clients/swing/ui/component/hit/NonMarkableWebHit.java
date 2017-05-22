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
package com.samebug.clients.swing.ui.component.hit;

import com.samebug.clients.common.tracking.SolutionHit;
import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.base.listener.AncestorListenerAdapter;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.modules.DataService;
import net.miginfocom.swing.MigLayout;

import javax.swing.event.AncestorEvent;

public final class NonMarkableWebHit extends SamebugPanel implements IWebHit {

    public NonMarkableWebHit(final Model model) {
        DataService.putData(this, DataService.SolutionId, model.solutionId);

        final WebHitTitlePanel titlePanel = new WebHitTitlePanel(this, model);

        setLayout(new MigLayout("fillx", "0[300px]0", "0[]0"));

        add(titlePanel, "growx, cell 0 0");

        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                DataService.putData(NonMarkableWebHit.this, TrackingKeys.SolutionHit, new SolutionHit(
                        model.solutionId, DataService.getData(NonMarkableWebHit.this, DataService.SolutionHitIndex), model.solutionMatchLevel, model.documentId)
                );
                final String transactionId = DataService.getData(NonMarkableWebHit.this, TrackingKeys.SolutionTransaction);
                TrackingService.trace(SwingRawEvent.solutionDisplay(NonMarkableWebHit.this, transactionId));
            }
        });
    }
}


