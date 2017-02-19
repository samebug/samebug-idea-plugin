/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.unscramble.AnalyzeStacktraceUtil;
import com.samebug.clients.common.search.api.StackTraceListener;
import com.samebug.clients.common.search.matcher.StackTraceMatcher;
import com.samebug.clients.common.services.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.tracking.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

final public class AnalyzeDialog extends DialogWrapper {
    final static Logger LOGGER = Logger.getInstance(AnalyzeDialog.class);
    final Project myProject;
    JPanel panel;
    final JPanel warningPanel;

    AnalyzeStacktraceUtil.StacktraceEditorPanel myEditorPanel;

    public AnalyzeDialog(Project project) {
        super(project);
        myProject = project;
        panel = new JPanel();
        warningPanel = new JPanel();
        setTitle(SamebugBundle.message("samebug.menu.analyze.dialog.title"));
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(SamebugBundle.message("samebug.menu.analyze.dialog.description")), BorderLayout.NORTH);
        myEditorPanel = AnalyzeStacktraceUtil.createEditorPanel(myProject, myProject);
        myEditorPanel.pasteTextFromClipboard();
        panel.add(myEditorPanel, BorderLayout.CENTER);
        panel.add(warningPanel, BorderLayout.SOUTH);
        displayWarningIfNotStackTrace();
        return panel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        // TODO focus on search button
        return myEditorPanel.getEditorComponent();
    }

    // TODO call this on editor input/periodically
    protected void displayWarningIfNotStackTrace() {
        final String trace = myEditorPanel.getText();
        boolean hasStackTrace = new Parser().hasStackTrace(trace);
        warningPanel.removeAll();
        if (!hasStackTrace) {
            JLabel warn = new JLabel(SamebugBundle.message("samebug.menu.analyze.dialog.warn"));
            warningPanel.add(warn);
        }
        panel.revalidate();
        panel.repaint();

    }

    @NotNull
    protected Action[] createActions() {
        return new Action[]{getCancelAction(), new SamebugSearch()};
    }

    final protected class SamebugSearch extends DialogWrapperAction implements DumbAware {

        public SamebugSearch() {
            super(SamebugBundle.message("samebug.menu.analyze.dialog.samebugButton"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            Tracking.appTracking().trace(Events.searchInSearchDialog());
            final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
            final ClientService client = plugin.getClient();
            final String trace = myEditorPanel.getText();
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    // TODO
//                    try {
//                        SearchResults result = client.searchSolutions(trace);
//                        try {
//                            int searchId = result.getSearchId();
//                            URL url = plugin.getUrlBuilder().search(searchId);
//                            BrowserUtil.browse(url);
//                            Tracking.appTracking().trace(Events.searchSucceedInSearchDialog(searchId));
//                        } catch (java.lang.Exception e1) {
//                            LOGGER.warn("Failed to open browser for search " + result.getSearchId(), e1);
//                        }
//                    } catch (SamebugClientException e1) {
//                        LOGGER.warn("Failed to execute search", e1);
//                    }
                }
            });
        }
    }

    // TODO using the serious parser, get the typename and message and use them for google search
    final protected class GoogleSearch extends DialogWrapperAction implements DumbAware {

        public GoogleSearch() {
            super(SamebugBundle.message("samebug.menu.analyze.dialog.googleButton"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            final String trace = myEditorPanel.getText();
            try {
                URL url = new URL("https://www.google.hu/search?q=" + trace);
                BrowserUtil.browse(url);
            } catch (MalformedURLException e1) {
                LOGGER.warn("Failed to open browser for google search", e1);
            }
        }
    }

    // TODO get the serious parser
    final protected class Parser implements StackTraceListener {
        final StackTraceMatcher parser;
        boolean found;

        public Parser() {
            this.parser = new StackTraceMatcher(this);
        }

        public boolean hasStackTrace(String text) {
            found = false;
            parser.append(text);
            parser.end();
            return found;
        }

        @Override
        public void stacktraceFound(String stacktrace) {
            found = true;
        }
    }
}
