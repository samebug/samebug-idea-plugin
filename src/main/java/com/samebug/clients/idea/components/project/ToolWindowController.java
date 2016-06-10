package com.samebug.clients.idea.components.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.ConnectionStatusListener;
import com.samebug.clients.idea.messages.view.FocusListener;
import com.samebug.clients.idea.messages.view.SearchViewListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.controller.HistoryTabController;
import com.samebug.clients.idea.ui.controller.SearchTabController;
import com.samebug.clients.search.api.entities.Solutions;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ToolWindowController extends AbstractProjectComponent implements FocusListener, SearchViewListener {
    final static Logger LOGGER = Logger.getInstance(ToolWindowController.class);
    @NotNull
    final Project project;
    @NotNull
    final Map<Integer, SearchTabController> activeSearches;
    @Nullable
    Integer focusedSearch = null;
    @NotNull
    final HistoryTabController historyTabController;


    protected ToolWindowController(Project project) {
        super(project);
        this.project = project;
        activeSearches = new HashMap<Integer, SearchTabController>();
        historyTabController = new HistoryTabController(project);
    }

    @NotNull
    public HistoryTabController getHistoryTabController() {
        return historyTabController;
    }

    @Override
    public void focusOnHistory() {
        final ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
        final ContentManager toolwindowCM = toolWindow.getContentManager();
        final Content content = toolwindowCM.getContent(historyTabController.getControlPanel());
        if (content != null) toolwindowCM.setSelectedContent(content);
        toolWindow.show(null);
    }

    @NotNull
    public void focusOnSearch(final int searchId) {
        final ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
        final ContentManager toolwindowCM = toolWindow.getContentManager();
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
            Content content = contentFactory.createContent(tab.getControlPanel(), SamebugBundle.message("samebug.toolwindow.search.tabName"), false);
            toolwindowCM.addContent(content);
            activeSearches.put(searchId, tab);
            focusedSearch = searchId;
            MessageBusConnection appMessageBus = ApplicationManager.getApplication().getMessageBus().connect(project);
            appMessageBus.subscribe(ConnectionStatusListener.CONNECTION_STATUS_TOPIC, tab.getStatusUpdater());
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
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            searchTab.update(solutions);
                        }
                    });
                } catch (SamebugClientException e) {
                    LOGGER.warn("Failed to download solutions", e);
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            searchTab.update(null);
                        }
                    });
                }

            }
        });
        toolWindow.show(null);
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

    @Override
    public void reload() {
        if (focusedSearch != null) {
            SearchTabController tab = activeSearches.get(focusedSearch);
            tab.refreshPane();
        }
    }
}
