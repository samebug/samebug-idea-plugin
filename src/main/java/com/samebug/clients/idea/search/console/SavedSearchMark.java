/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.search.console;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.entities.search.SavedSearch;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.messages.FocusListener;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.SamebugIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

final class SavedSearchMark extends SearchMark {
    private final SavedSearch search;

    public SavedSearchMark(SavedSearch search) {
        this.search = search;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return SamebugIcons.gutterSamebug;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SavedSearchMark) {
            SavedSearchMark rhs = (SavedSearchMark) o;
            return rhs.search.equals(search);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return search.hashCode();
    }

    @Override
    public boolean isNavigateAction() {
        return true;
    }

    @Override
    @NotNull
    public String getTooltipText() {
        return "Search " + search.getSavedSearch().getSearchId()
                + "\nClick to show solutions.";
    }

    @NotNull
    public AnAction getClickAction() {
        return new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                Integer searchId = search.getSavedSearch().getSearchId();
                Project project = getEventProject(e);
                if (project != null) {
                    Tracking.projectTracking(project).trace(Events.gutterIconClicked(searchId));
                    project.getMessageBus().syncPublisher(FocusListener.TOPIC).focusOnSearch(searchId);
                }
            }
        };
    }
}

