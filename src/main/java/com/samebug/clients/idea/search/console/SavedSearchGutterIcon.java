/*
 * Copyright 2018 Samebug, Inc.
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
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.idea.messages.FocusListener;
import com.samebug.clients.idea.tracking.IdeaRawEvent;
import com.samebug.clients.swing.ui.modules.IconService;
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
        switch (search.getSolutionType()) {
            case TIP:
                return IconService.gutterTip;
            case HELP_REQUEST:
                return IconService.gutterHelpRequest;
            case BUGMATE:
                return IconService.gutterSamebug;
            case WEBHIT:
                return IconService.gutterSamebug;
            default:
                return IconService.gutterSamebug;
        }
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
        switch (search.getSolutionType()) {
            case TIP:
                return MessageService.message("samebug.gutter.savedSearch.hasTip.tooltip");
            case HELP_REQUEST:
                return MessageService.message("samebug.gutter.savedSearch.hasHelpRequest.tooltip");
            case BUGMATE:
                return MessageService.message("samebug.gutter.savedSearch.hasBugmates.tooltip");
            case WEBHIT:
                return MessageService.message("samebug.gutter.savedSearch.hasSolutions.tooltip");
            default:
                return MessageService.message("samebug.gutter.savedSearch.nothing.tooltip");
        }
    }

    @NotNull
    public AnAction getClickAction() {
        return new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                Integer searchId = search.getSearchId();
                Project project = getEventProject(e);
                if (project != null) {
                    final String solutionType;
                    switch (search.getSolutionType()) {
                        case TIP:
                            solutionType = "tip";
                            break;
                        case HELP_REQUEST:
                            solutionType = "help-request";
                            break;
                        case BUGMATE:
                            solutionType = "bugmate";
                            break;
                        case WEBHIT:
                            solutionType = "webhit";
                            break;
                        default:
                            solutionType = "none";
                            break;
                    }

                    project.getMessageBus().syncPublisher(FocusListener.TOPIC).focusOnSearch(searchId);
                    TrackingService.trace(IdeaRawEvent.gutterIconClick(searchId, solutionType));
                }
            }
        };
    }
}

