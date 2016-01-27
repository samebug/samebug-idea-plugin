package com.samebug.clients.idea;

import com.intellij.execution.Executor;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.intellij.autosearch.AutomatedSolutionSearch;
import com.samebug.clients.idea.intellij.autosearch.android.AndroidShellSolutionSearch;
import com.samebug.clients.idea.intellij.autosearch.console.ConsoleScannerSolutionSearch;
import com.samebug.clients.idea.intellij.settings.SetupDialog;
import com.samebug.clients.idea.intellij.toolwindow.SamebugToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SamebugProjectComponent implements ProjectComponent {

    public SamebugProjectComponent(final Project project) {
        this.project = project;
    }

    /**
     * Subscribe a @see com.samebug.clients.idea.intellij.ProcessOutputScannerInstaller on the project message bus
     */
    @Override
    public void projectOpened() {
        if (!SamebugIdeaPlugin.isInitialized())
            SetupDialog.setup();
        this.samebugSearch = new ConsoleScannerSolutionSearch(project);
        createToolWindow();

        MessageBusConnection messageBusConnection = project.getMessageBus().connect(project);
        AndroidShellSolutionSearch s = SamebugIdeaPlugin.getAndroidShellSolutionSearch();
        if (s != null) {
            messageBusConnection.subscribe(RunContentManager.TOPIC, s);
        }
    }

    private void createToolWindow() {
        this.toolWindowManager = new SamebugToolWindowManager(project);
    }


    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }


    @Override
    public void projectClosed() {
    }

    private final Project project;
    private SamebugToolWindowManager toolWindowManager;
    private AutomatedSolutionSearch samebugSearch;


    private final static Logger logger = Logger.getInstance(SamebugProjectComponent.class);
}

