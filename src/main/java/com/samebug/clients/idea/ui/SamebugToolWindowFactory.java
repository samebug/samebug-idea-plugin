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
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.IdeaSamebugClient;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.search.api.entities.History;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SamebugToolWindowFactory implements ToolWindowFactory, BatchStackTraceSearchListener {
    private JPanel contentPanel;
    private JPanel toolbarPanel;
    private JEditorPane historyPane;
    private JScrollPane scrollPane;
    private ToolWindow toolWindow;
    private Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        try {
            initContent();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        initHistoryPane();
    }

    private void initHistoryPane() {
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(project);
        messageBusConnection.subscribe(BatchStackTraceSearchListener.BATCH_SEARCH_TOPIC, this);
        loadHistory();
    }

    private void initContent() throws MalformedURLException {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(contentPanel, SamebugBundle.message("samebug.toolwindow.displayName"), false);
        toolWindow.getContentManager().addContent(content);
        HTMLEditorKit kit = new HTMLEditorKit();
        historyPane.setEditorKit(kit);
        StyleSheet ss = kit.getStyleSheet();
        ss.importStyleSheet(new URL("http://localhost:9000/assets-v/style/intellij.css"));
        kit.setStyleSheet(ss);
        historyPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URL url = e.getURL();
                    BrowserUtil.browse(url);
                }
            }
        });
    }


    private final static Logger LOGGER = Logger.getInstance(SamebugToolWindowFactory.class);

    private void createUIComponents() {
        this.toolbarPanel = createToolbarPanel();
    }

    private JPanel createToolbarPanel() {
        final DefaultActionGroup group = (DefaultActionGroup) ActionManager.getInstance().getAction("Samebug.ToolWindowMenu");
        group.addAction(new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                loadHistory();
            }
        });
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
        return buttonsPanel;
    }

    private void refreshHistoryPane(final History history) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                HTMLEditorKit kit = (HTMLEditorKit) historyPane.getEditorKit();
                StyleSheet ss = kit.getStyleSheet();
                kit.setStyleSheet(ss);
                historyPane.setText(history.html);
                historyPane.setCaretPosition(0);
            }
        });
    }

    private void loadHistory() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                final IdeaSamebugClient client = IdeaSamebugClient.getInstance();
                try {
                    final History history = client.getSearchHistory();
                    refreshHistoryPane(history);
                } catch (SamebugClientException e1) {
                    LOGGER.error("Failed to retrieve history", e1);
                }
            }
        });
    }

    @Override
    public void batchStart() {

    }

    @Override
    public void batchFinished(List<SearchResults> results, int failed) {
        loadHistory();
    }
}
