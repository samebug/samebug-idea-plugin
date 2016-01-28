package com.samebug.clients.idea.intellij.toolwindow;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.samebug.clients.idea.SamebugIdeaPlugin;
import com.samebug.clients.idea.intellij.autosearch.android.AndroidShellSolutionSearch;
import com.samebug.clients.idea.intellij.autosearch.console.ConsoleScannerStackTraceSearch;
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
    private ConsoleScannerStackTraceSearch consoleSearch;
    private AndroidShellSolutionSearch androidShellSolutionSearch;
    private Project project;

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
        this.project = project;
        this.toolWindow = toolWindow;
        initContent();
    }

    private void initContent() {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(contentPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }


    private final static Logger logger = Logger.getInstance(SamebugToolWindowFactory.class);

}
