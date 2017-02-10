/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.controller.expsearch;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.search.api.entities.RestHit;
import com.samebug.clients.common.search.api.entities.SolutionReference;
import com.samebug.clients.common.search.api.entities.Solutions;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.component.profile.ProfilePanel;
import com.samebug.clients.idea.ui.component.solutions.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final public class SolutionFrameController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(SolutionFrameController.class);
    @NotNull
    final ToolWindowController twc;
    @NotNull
    final Project myProject;

    @NotNull
    final SolutionFrame view;

    @NotNull
    final ViewController viewController;
    @NotNull
    final ModelController modelController;

    public SolutionFrameController(@NotNull ToolWindowController twc, @NotNull Project project,
                                   @NotNull SolutionService service,
                                   final int searchId) {
        this.twc = twc;
        this.myProject = project;

        view = new SolutionFrame(myProject.getMessageBus());

        viewController = new ViewController(this);
        modelController = new ModelController(this);
    }

    @NotNull
    public JPanel getControlPanel() {
        return view;
    }

    @Override
    public void dispose() {

    }

    SolutionFrame.Model convert(@NotNull Solutions solutions) {
        final List<WebHit.Model> webhits = new ArrayList<WebHit.Model>(solutions.getReferences().size());
        for (RestHit<SolutionReference> externalHit : solutions.getReferences()) {
            SolutionReference externalSolution = externalHit.getSolution();
            MarkPanel.Model mark = new MarkPanel.Model(externalHit.getScore(), externalHit.getMarkId(), true /*TODO*/);
            final String sourceIconName = externalSolution.getSource().getIcon();
            final URL sourceIconUrl = IdeaSamebugPlugin.getInstance().getUrlBuilder().sourceIcon(sourceIconName);

            String createdBy = null;
            if (externalSolution.getAuthor() != null) createdBy = externalSolution.getAuthor().getName();
            WebHit.Model webhit = new WebHit.Model(externalSolution.getTitle(), externalSolution.getUrl(), externalSolution.getCreatedAt(), createdBy, externalSolution.getSource().getName(), sourceIconUrl, mark);
            webhits.add(webhit);
        }

        WebResultsTab.Model webResults = new WebResultsTab.Model(webhits);
        WriteTipCTA.Model cta = new WriteTipCTA.Model(0);
        TipResultsTab.Model tipResults = new TipResultsTab.Model(Collections.<TipHit.Model>emptyList(), cta);
        ResultTabs.Model resultTabs = new ResultTabs.Model(webResults, tipResults);
        ExceptionHeaderPanel.Model header = new ExceptionHeaderPanel.Model("");
        ProfilePanel.Model profile = new ProfilePanel.Model(0, 0, 0, 0, "", null);
        SolutionFrame.Model model = new SolutionFrame.Model(resultTabs, header, profile);

        return model;
    }

}

