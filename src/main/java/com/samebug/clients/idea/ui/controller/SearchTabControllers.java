/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.search.api.entities.legacy.Solutions;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by poroszd on 3/29/16.
 */
public class SearchTabControllers {
    final static Logger LOGGER = Logger.getInstance(SearchTabController.class);
    final private Project project;
    final private Map<Integer, SearchTabController> activeSearches;

    public SearchTabControllers(Project project) {
        this.project = project;
        activeSearches = new HashMap<Integer, SearchTabController>();
    }

    public SearchTabController openSearchTab(final int searchId) {
        final ContentManager toolwindowCM = ToolWindowManager.getInstance(project).getToolWindow("Samebug").getContentManager();
        SearchTabController tab = activeSearches.get(searchId);
        // FIXME: for now, we let at most one search tab
        for (Map.Entry<Integer, SearchTabController> opened : activeSearches.entrySet()) {
            if (opened.getKey().equals(searchId)) break;
            else {
                Content content = toolwindowCM.getContent(opened.getValue().getControlPanel());
                toolwindowCM.removeContent(content, true);
                activeSearches.remove(opened.getKey());
            }
        }

        if (tab == null) {
            tab = new SearchTabController(project);
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(tab.getControlPanel(), SamebugBundle.message("samebug.toolwindow.search.tabName", searchId), false);
            toolwindowCM.addContent(content);
            activeSearches.put(searchId, tab);
            toolwindowCM.setSelectedContent(content);

        } else {
            Content content = toolwindowCM.getContent(tab.getControlPanel());
            toolwindowCM.setSelectedContent(content);
        }
        final SearchTabController searchTab = tab;
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Solutions solutions = IdeaSamebugPlugin.getInstance().getClient().getSolutions(searchId);
                    searchTab.update(solutions);
                } catch (SamebugClientException e) {
                    LOGGER.warn("Failed to download solutions", e);
                }

            }
        });
        return tab;
    }

    // TODO add close action to tab which calls this method
    public void closeSearchTab(int searchId) {
        ContentManager toolwindowCM = ToolWindowManager.getInstance(project).getToolWindow("Samebug").getContentManager();
        SearchTabController tab = activeSearches.get(searchId);
        if (tab != null) {
            Content history = toolwindowCM.getContent(0);
            toolwindowCM.requestFocus(history, true);
            Content content = toolwindowCM.getContent(tab.getControlPanel());
            toolwindowCM.removeContent(content, true);
            activeSearches.remove(searchId);
        }
    }
}
