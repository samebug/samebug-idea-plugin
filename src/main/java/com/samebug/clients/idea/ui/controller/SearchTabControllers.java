package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.samebug.clients.idea.resources.SamebugBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by poroszd on 3/29/16.
 */
public class SearchTabControllers {
    final private Project project;
    final private Map<Integer, SearchTabController> activeSearches;

    public SearchTabControllers(Project project) {
        this.project = project;
        activeSearches = new HashMap<Integer, SearchTabController>();
    }

    public SearchTabController openSearchTab(int searchId) {
        ContentManager toolwindowCM = ToolWindowManager.getInstance(project).getToolWindow("Samebug").getContentManager();
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
            toolwindowCM.requestFocus(content, true);
        }
        tab.load(searchId);
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
