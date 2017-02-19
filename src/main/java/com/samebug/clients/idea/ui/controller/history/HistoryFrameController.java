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
package com.samebug.clients.idea.ui.controller.history;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.search.api.entities.Search;
import com.samebug.clients.common.search.api.entities.SearchGroup;
import com.samebug.clients.common.search.api.entities.SearchHistory;
import com.samebug.clients.common.search.api.entities.StackTraceSearch;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.HistoryService;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.component.history.HistoryTabView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

final public class HistoryFrameController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(HistoryFrameController.class);
    @NotNull
    final ToolWindowController twc;
    @NotNull
    final Project myProject;
    @NotNull
    final HistoryTabView view;
    @NotNull
    final HistoryService service;

    @NotNull
    final ViewController viewController;
    @NotNull
    final ModelController modelController;

    public HistoryFrameController(@NotNull ToolWindowController twc, @NotNull Project project,
                                  @NotNull HistoryService service) {
        this.twc = twc;
        this.myProject = project;
        this.service = service;

        view = new HistoryTabView(myProject.getMessageBus());

        viewController = new ViewController(this);
        modelController = new ModelController(this);

        try {
            service.loadSearchHistory();
        } catch (SamebugClientException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public JPanel getControlPanel() {
        return view;
    }

    @Override
    public void dispose() {
    }

    List<HistoryTabView.Card.Model> convert(@NotNull SearchHistory history) {
        final List<HistoryTabView.Card.Model> models = new ArrayList<HistoryTabView.Card.Model>(history.getSearchGroups().size());
        for (SearchGroup group : history.getSearchGroups()) {
            HistoryTabView.Card.Model model;
            Search search = group.getLastSearch();
            if (search instanceof StackTraceSearch) {
                StackTraceSearch s = (StackTraceSearch) search;
                model = new HistoryTabView.Card.Model(s.getStackTrace().getTrace().getTypeName(), search.getId());
            } else {
                model = new HistoryTabView.Card.Model("not parseable", search.getId());
            }
            models.add(model);
        }
        return models;
    }
}
