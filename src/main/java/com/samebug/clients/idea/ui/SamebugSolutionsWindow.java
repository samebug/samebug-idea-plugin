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
package com.samebug.clients.idea.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.search.api.entities.tracking.Solutions;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.net.URL;
import java.util.Dictionary;

/**
 * Created by poroszd on 2/24/16.
 */
public class SamebugSolutionsWindow {
    private JPanel controlPanel;
    private JScrollPane scrollPane;
    private JEditorPane solutionsPane;
    private Project project;

    private final static Logger LOGGER = Logger.getInstance(SamebugSolutionsWindow.class);

    public SamebugSolutionsWindow(Project project) {
        this.project = project;
    }

    public JComponent getControlPanel() {
        return controlPanel;
    }

    public void initSolutionsPane() {
        HTMLEditorKit kit = new HTMLEditorKit();
        solutionsPane.setEditorKit(kit);
        solutionsPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URL url = e.getURL();
                    BrowserUtil.browse(url);
                    Tracking.projectTracking(project).trace(Events.linkClick(project, url));
                }
            }
        });
        if ((Dictionary) solutionsPane.getDocument().getProperty("imageCache") == null) {
            solutionsPane.getDocument().putProperty("imageCache", ImageUtil.imageCache);
        }
    }

    public void loadSolutions(final String searchId) {
        final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        if (plugin.isInitialized()) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        emptySolutionsPane();
                        final Solutions solutions = plugin.getClient().getSolutions(searchId);
                        CssUtil.updatePaneStyleSheet(solutionsPane);
                        refreshSolutionsPane(solutions);
                    } catch (SamebugClientException e1) {
                        LOGGER.warn("Failed to retrieve solutions for search " + searchId, e1);
                    }
                }
            });
        }
    }

    private void emptySolutionsPane() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                solutionsPane.setText("");
            }
        });
    }

    private void refreshSolutionsPane(final Solutions solutions) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                HTMLEditorKit kit = (HTMLEditorKit) solutionsPane.getEditorKit();
                solutionsPane.setText(solutions.html);
                solutionsPane.setCaretPosition(0);
            }
        });
    }

}
