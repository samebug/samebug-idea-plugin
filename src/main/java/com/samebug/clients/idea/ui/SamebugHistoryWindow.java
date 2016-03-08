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
///**
// * Copyright 2016 Samebug, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.samebug.clients.idea.ui;
//
//import com.intellij.openapi.actionSystem.ActionManager;
//import com.intellij.openapi.actionSystem.ActionPlaces;
//import com.intellij.openapi.actionSystem.ActionToolbar;
//import com.intellij.openapi.actionSystem.DefaultActionGroup;
//import com.intellij.openapi.application.ApplicationManager;
//import com.intellij.openapi.diagnostic.Logger;
//import com.intellij.openapi.project.Project;
//import com.intellij.util.ui.UIUtil;
//import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
//import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
//import com.samebug.clients.idea.messages.ConnectionStatusListener;
//import com.samebug.clients.idea.resources.SamebugBundle;
//import com.samebug.clients.idea.resources.SamebugIcons;
//import com.samebug.clients.idea.ui.views.SearchGroupCardView;
//import com.samebug.clients.search.api.SamebugClient;
//import com.samebug.clients.search.api.entities.History;
//import com.samebug.clients.search.api.entities.SearchResults;
//import com.samebug.clients.search.api.exceptions.SamebugClientException;
//
//import javax.swing.*;
//import java.awt.*;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//
///**
// * Created by poroszd on 2/14/16.
// */
//public class SamebugHistoryWindow implements BatchStackTraceSearchListener, ConnectionStatusListener {
//    private JPanel controlPanel;
//    private JPanel toolbarPanel;
//    private JScrollPane scrollPane;
//    private JPanel historyPane;
//    private JLabel statusIcon;
//    private JPanel statusToolbarPanel;
//    final private Project project;
//    final private SamebugSolutionsWindow solutionsWindow;
//    private boolean recentFilterOn;
//
//    private final static Logger LOGGER = Logger.getInstance(SamebugHistoryWindow.class);
//
//    public SamebugHistoryWindow(Project project, SamebugSolutionsWindow solutionsWindow) {
//        this.project = project;
//        this.solutionsWindow = solutionsWindow;
//
//        controlPanel = new JPanel(new BorderLayout(0, 0));
//        toolbarPanel = createToolbarPanel();
//        historyPane = new JPanel();
//        statusIcon = new JLabel();
//        final SearchGroupCardView g = new SearchGroupCardView();
//        historyPane.add(g.controlPane);
//        controlPanel.add(g.controlPane, BorderLayout.CENTER);
//        g.breadcrumbBar.setBackground(UIUtil.getPanelBackground());
//        g.breadcrumbBar.addPropertyChangeListener("background", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                g.breadcrumbBar.setBackground(UIUtil.getPanelBackground());
//            }
//        });
//    }
//
//    public JComponent getControlPanel() {
//        return controlPanel;
//    }
//
//    public void initHistoryPane() {
//        loadHistory();
//        statusIcon.setIcon(null);
//    }
//
//    public void loadHistory() {
//        final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
//        if (plugin.isInitialized()) {
//            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        emptyHistoryPane();
//                        final History history = plugin.getClient().getSearchHistory(recentFilterOn);
//                        refreshHistoryPane(history);
//                    } catch (SamebugClientException e1) {
//                        LOGGER.warn("Failed to retrieve history", e1);
//                    }
//                }
//            });
//        }
//    }
//
//    @Override
//    public void batchStart() {
//
//    }
//
//    @Override
//    public void batchFinished(java.util.List<SearchResults> results, int failed) {
//        loadHistory();
//    }
//
//    private JPanel createToolbarPanel() {
//        final DefaultActionGroup group = (DefaultActionGroup) ActionManager.getInstance().getAction("Samebug.ToolWindowMenu");
//        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
//        final JPanel buttonsPanel = new JPanel(new BorderLayout());
//        buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
//        return buttonsPanel;
//    }
//
//    private void emptyHistoryPane() {
//        ApplicationManager.getApplication().invokeLater(new Runnable() {
//            public void run() {
//                historyPane.removeAll();
//            }
//        });
//    }
//
//    private void refreshHistoryPane(final History history) {
//        ApplicationManager.getApplication().invokeLater(new Runnable() {
//            public void run() {
//                SearchGroupCardView g = new SearchGroupCardView();
//                historyPane.add(g.controlPane);
//                historyPane.invalidate();
//            }
//        });
//    }
//
//    @Override
//    public void startRequest() {
//        ApplicationManager.getApplication().invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                statusIcon.setIcon(SamebugIcons.linkActive);
//                statusIcon.setToolTipText(SamebugBundle.message("samebug.toolwindow.history.connectionStatus.description.loading"));
//                statusIcon.invalidate();
//            }
//        });
//    }
//
//    @Override
//    public void finishRequest(final boolean isConnected) {
//        ApplicationManager.getApplication().invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                if (IdeaSamebugPlugin.getInstance().getClient().getNumberOfActiveRequests() == 0) {
//                    if (isConnected) {
//                        statusIcon.setIcon(null);
//                        statusIcon.setToolTipText(null);
//                    } else {
//                        statusIcon.setIcon(SamebugIcons.linkError);
//                        statusIcon.setToolTipText(SamebugBundle.message("samebug.toolwindow.history.connectionStatus.description.notConnected", SamebugClient.root));
//                    }
//                    statusIcon.invalidate();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void authenticationChange(final boolean isAuthorized) {
//    }
//}
