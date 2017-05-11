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
package com.samebug.clients.idea.search.console;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.entities.search.SavedSearch;
import com.samebug.clients.idea.messages.FocusListener;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.modules.IconService;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

final class SavedSearchGutterIcon extends SearchMark implements DumbAware {
    private final SavedSearch search;

    SavedSearchGutterIcon(SavedSearch search) {
        this.search = search;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        if (search.hasTip()) return IconService.gutterTip;
        else if (search.hasHelpRequest()) return IconService.gutterHelpRequest;
        else if (search.hasBugmate()) return IconService.gutterSamebug;
        else if (search.hasWebHit()) return IconService.gutterSamebug;
        return IconService.gutterSamebug;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SavedSearchGutterIcon) {
            SavedSearchGutterIcon rhs = (SavedSearchGutterIcon) o;
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
        TrackingService.trace(Events.gutterIconHover(search.getSearchId()));
        if (search.hasTip()) return MessageService.message("samebug.gutter.savedSearch.hasTip.tooltip");
        else if (search.hasHelpRequest()) return MessageService.message("samebug.gutter.savedSearch.hasHelpRequest.tooltip");
        else if (search.hasBugmate()) return MessageService.message("samebug.gutter.savedSearch.hasBugmates.tooltip");
        else if (search.hasWebHit()) return MessageService.message("samebug.gutter.savedSearch.hasSolutions.tooltip");
        return MessageService.message("samebug.gutter.savedSearch.nothing.tooltip");
    }

    @NotNull
    public AnAction getClickAction() {
        return new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                Integer searchId = search.getSearchId();
                Project project = getEventProject(e);
                if (project != null) {
                    TrackingService.trace(Events.gutterIconClicked(searchId));
                    project.getMessageBus().syncPublisher(FocusListener.TOPIC).focusOnSearch(searchId);
                }
            }
        };
    }
}

