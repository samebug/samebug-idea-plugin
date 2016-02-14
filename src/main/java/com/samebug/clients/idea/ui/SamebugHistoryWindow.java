package com.samebug.clients.idea.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.History;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.net.URL;

/**
 * Created by poroszd on 2/14/16.
 */
public class SamebugHistoryWindow implements BatchStackTraceSearchListener {
    private JPanel controlPanel;
    private JPanel toolbarPanel;
    private JScrollPane scrollPane;
    private JEditorPane historyPane;
    private Project project;
    private final static Logger LOGGER = Logger.getInstance(SamebugHistoryWindow.class);

    public SamebugHistoryWindow(Project project) {
        this.project = project;
    }

    public JComponent getControlPanel() {
        return controlPanel;
    }

    public void loadHistory() {
        final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        if (plugin.getState().isInitialized()) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final History history = plugin.getClient().getSearchHistory();
                        refreshHistoryPane(history);
                    } catch (SamebugClientException e1) {
                        LOGGER.error("Failed to retrieve history", e1);
                    }
                }
            });
        }
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

    @Override
    public void batchStart() {

    }

    @Override
    public void batchFinished(java.util.List<SearchResults> results, int failed) {
        loadHistory();
    }

    public void initHistoryPane() {
        HTMLEditorKit kit = new HTMLEditorKit();
        historyPane.setEditorKit(kit);
        historyPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URL url = e.getURL();
                    BrowserUtil.browse(url);
                }
            }
        });
        setCssTheme(UIManager.getLookAndFeel().getName());
        loadHistory();
    }

    public void setCssTheme(String themeName) {
        HTMLEditorKit kit = (HTMLEditorKit) historyPane.getEditorKit();
        StyleSheet ss = kit.getStyleSheet();
        final SamebugClient client = IdeaSamebugPlugin.getInstance().getClient();
        String themeId;
        if (themeName == "IntelliJ") {
            themeId = "intellij";
        } else if (themeName == "Darcula") {
            themeId = "darcula";
        } else {
            themeId = "intellij";
        }
        ss.importStyleSheet(client.getHistoryCssUrl(themeId));
        kit.setStyleSheet(ss);
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
}
