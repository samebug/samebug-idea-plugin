package com.samebug.clients.idea.intellij.toolwindow;

import com.intellij.execution.ui.RunContentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.SamebugIdeaPlugin;
import com.samebug.clients.idea.intellij.autosearch.android.AndroidShellSolutionSearch;
import com.samebug.clients.idea.intellij.autosearch.console.ConsoleScannerSolutionSearch;
import com.samebug.clients.idea.intellij.settings.SettingsDialog;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SamebugToolWindowFactory implements ToolWindowFactory {
    private JButton settingsButton;
    private JPanel contentPanel;
    private JToolBar toolbar;
    private ToolWindow toolWindow;

    public SamebugToolWindowFactory() {
        if (!SamebugIdeaPlugin.isInitialized())
            SettingsDialog.setup();

        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SettingsDialog.setup();
            }
        });
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        initContent(toolWindow);
        initSearchEngine(project);
    }

    private void initContent(@NotNull ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(contentPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void initSearchEngine(@NotNull Project project) {
        ConsoleScannerSolutionSearch consoleSearch = new ConsoleScannerSolutionSearch(project);

        AndroidShellSolutionSearch androidShellSolutionSearch = SamebugIdeaPlugin.getAndroidShellSolutionSearch();
        if (androidShellSolutionSearch != null) {
            MessageBusConnection messageBusConnection = project.getMessageBus().connect();
            messageBusConnection.subscribe(RunContentManager.TOPIC, androidShellSolutionSearch);
        }
    }
}
