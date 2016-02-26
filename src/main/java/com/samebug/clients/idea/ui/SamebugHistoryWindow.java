/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.idea.messages.ConnectionStatusListener;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.search.api.entities.History;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.net.URL;
import java.util.Dictionary;

/**
 * Created by poroszd on 2/14/16.
 */
public class SamebugHistoryWindow implements BatchStackTraceSearchListener, ConnectionStatusListener {
    private JPanel controlPanel;
    private JPanel toolbarPanel;
    private JScrollPane scrollPane;
    private JEditorPane historyPane;
    private JLabel statusIcon;
    final private Project project;
    final private SamebugSolutionsWindow solutionsWindow;
    private boolean recentFilterOn;

    private final static Logger LOGGER = Logger.getInstance(SamebugHistoryWindow.class);

    public SamebugHistoryWindow(Project project, SamebugSolutionsWindow solutionsWindow) {
        this.project = project;
        this.solutionsWindow = solutionsWindow;
    }

    public JComponent getControlPanel() {
        return controlPanel;
    }

    public void initHistoryPane() {
        HTMLEditorKit kit = new HTMLEditorKit();
        historyPane.setEditorKit(kit);
        historyPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URL url = e.getURL();
//                    String searchId = (String) ((SimpleAttributeSet) e.getSourceElement().getAttributes().getAttribute(HTML.Tag.A)).getAttribute("data-search-id");
//                    if (searchId != null) {
//                        solutionsWindow.loadSolutions(searchId);
//                        ToolWindow w = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
//                        w.getContentManager().setSelectedContent(w.getContentManager().getContent(solutionsWindow.getControlPanel()), true);
//                    } else {
//                        BrowserUtil.browse(url);
//                    }
                    BrowserUtil.browse(url);
                    Tracking.projectTracking(project).trace(Events.linkClick(project, url));
                }
            }
        });

        if ((Dictionary) historyPane.getDocument().getProperty("imageCache") == null) {
            historyPane.getDocument().putProperty("imageCache", HtmlUtil.imageCache);
        }
        loadHistory();
        statusIcon.setIcon(null);
    }

    public void loadHistory() {
        final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        if (plugin.isInitialized()) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        emptyHistoryPane();
                        final History history = plugin.getClient().getSearchHistory(recentFilterOn);
                        cssUtil.updatePaneStyleSheet(historyPane);
                        refreshHistoryPane(history);
                    } catch (SamebugClientException e1) {
                        LOGGER.warn("Failed to retrieve history", e1);
                    }
                }
            });
        }
    }

    @Override
    public void batchStart() {

    }

    @Override
    public void batchFinished(java.util.List<SearchResults> results, int failed) {
        loadHistory();
    }

    private void createUIComponents() {
        this.toolbarPanel = createToolbarPanel();
    }

    private JPanel createToolbarPanel() {
        final DefaultActionGroup group = (DefaultActionGroup) ActionManager.getInstance().getAction("Samebug.ToolWindowMenu");
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
        return buttonsPanel;
    }

    private void emptyHistoryPane() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                historyPane.setText("");
            }
        });
    }

    private void refreshHistoryPane(final History history) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                HTMLEditorKit kit = (HTMLEditorKit) historyPane.getEditorKit();
                historyPane.setText(history.html);
                historyPane.setCaretPosition(0);
            }
        });
    }

    @Override
    public void startRequest() {

    }

    @Override
    public void finishRequest(final boolean isConnected) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    statusIcon.setIcon(null);
                    statusIcon.setText(null);
                } else {
                    statusIcon.setIcon(SamebugIcons.linkError);
                    statusIcon.setToolTipText("Connection lost");
                }
            }
        });
    }

    @Override
    public void authorizationChange(final boolean isAuthorized) {
    }
}
