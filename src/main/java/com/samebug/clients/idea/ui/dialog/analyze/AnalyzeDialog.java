/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.dialog.analyze;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.unscramble.AnalyzeStacktraceUtil;
import com.samebug.clients.common.search.StackTraceListener;
import com.samebug.clients.common.search.StackTraceMatcher;
import com.samebug.clients.common.services.SearchService;
import com.samebug.clients.common.tracking.Location;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.entities.jsonapi.CreatedSearchResource;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.search.StackTraceInfo;
import com.samebug.clients.http.exceptions.BadRequest;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.exceptions.UserUnauthenticated;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.FocusListener;
import com.samebug.clients.idea.ui.modules.BrowserUtil;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;

public final class AnalyzeDialog extends DialogWrapper {
    static final Logger LOGGER = Logger.getInstance(AnalyzeDialog.class);
    final Project myProject;
    JPanel panel;
    final JPanel warningPanel;
    final SamebugSearch searchAction;

    AnalyzeStacktraceUtil.StacktraceEditorPanel myEditorPanel;

    public AnalyzeDialog(Project project, String transactionId) {
        super(project);
        myProject = project;
        panel = new JPanel();
        warningPanel = new JPanel();
        searchAction = new SamebugSearch();
        setTitle(MessageService.message("samebug.menu.analyze.dialog.title"));
        DataService.putData(panel, TrackingKeys.SearchTransaction, transactionId);
        DataService.putData(panel, TrackingKeys.Location, new Location.SearchDialog());
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(MessageService.message("samebug.menu.analyze.dialog.description")), BorderLayout.NORTH);
        myEditorPanel = AnalyzeStacktraceUtil.createEditorPanel(myProject, myProject);
        myEditorPanel.pasteTextFromClipboard();
        panel.add(myEditorPanel, BorderLayout.CENTER);
        panel.add(warningPanel, BorderLayout.SOUTH);
        displayWarningIfNotStackTrace();
        return panel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return getButton(searchAction);
    }

    // IMPROVE call this on editor input/periodically
    protected void displayWarningIfNotStackTrace() {
        final String trace = myEditorPanel.getText();
        boolean hasStackTrace = new Parser().hasStackTrace(trace);
        warningPanel.removeAll();
        if (!hasStackTrace) {
            JLabel warn = new JLabel(MessageService.message("samebug.menu.analyze.dialog.warn"));
            warningPanel.add(warn);
        }
        panel.revalidate();
        panel.repaint();
    }

    // TODO should use a separate component, not the warningpanel
    protected void displayError(String message) {
        warningPanel.removeAll();
        warningPanel.add(new JLabel(message));
        panel.revalidate();
        panel.repaint();
    }

    @NotNull
    protected Action[] createActions() {
        return new Action[]{getCancelAction(), searchAction};
    }

    protected final class SamebugSearch extends DialogWrapperAction implements DumbAware {

        public SamebugSearch() {
            super(MessageService.message("samebug.menu.analyze.dialog.samebugButton"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            TrackingService.trace(SwingRawEvent.searchSubmit(panel, DataService.getData(panel, TrackingKeys.SearchTransaction)));
            final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
            final SearchService searchService = plugin.searchService;
            final String trace = myEditorPanel.getText();
            final JButton searchButton = getButton(searchAction);
            if (searchButton != null) {
                searchButton.setText("Searching...");
                searchButton.setEnabled(false);
            }
            try {
                // NOTE: this search post happens on the UI thread, but we own the UI thread as long as the dialog is opened.
                CreatedSearchResource result = searchService.search(trace);
                Search search = result.getData();
                final int searchId = search.getId();

                if (!(search.getQueryInfo() instanceof StackTraceInfo)) displayError(MessageService.message("samebug.menu.analyze.dialog.error.textSearch"));
                else {
                    myProject.getMessageBus().syncPublisher(FocusListener.TOPIC).focusOnSearch(searchId);
                    TrackingService.trace(SwingRawEvent.searchCreate(panel, DataService.getData(panel, TrackingKeys.SearchTransaction), search));
                    AnalyzeDialog.this.close(OK_EXIT_CODE);
                }
            } catch (BadRequest e1) {
                LOGGER.warn("Failed to execute search", e1);
                ToolWindowManager.getInstance(myProject).getToolWindow("Samebug").show(null);
                AnalyzeDialog.this.close(CANCEL_EXIT_CODE);
            } catch (UserUnauthenticated e1) {
                LOGGER.warn("Failed to execute search", e1);
                ToolWindowManager.getInstance(myProject).getToolWindow("Samebug").show(null);
                AnalyzeDialog.this.close(CANCEL_EXIT_CODE);
            } catch (SamebugClientException e1) {
                LOGGER.warn("Failed to execute search", e1);
                displayError(MessageService.message("samebug.menu.analyze.dialog.error.unhandled"));
            } finally {
                if (searchButton != null) {
                    searchButton.setEnabled(true);
                    searchButton.setText(MessageService.message("samebug.menu.analyze.dialog.samebugButton"));
                }
            }
        }
    }

    // IMPROVE using the serious parser, get the typename and message and use them for google search
    protected final class GoogleSearch extends DialogWrapperAction implements DumbAware {

        public GoogleSearch() {
            super(MessageService.message("samebug.menu.analyze.dialog.googleButton"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            final String trace = myEditorPanel.getText();
            URI uri = URI.create("https://www.google.hu/search?q=" + trace);
            BrowserUtil.browse(uri);
        }
    }

    // IMPROVE get the serious parser
    protected final class Parser implements StackTraceListener {
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
