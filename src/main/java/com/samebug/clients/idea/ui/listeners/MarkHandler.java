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
package com.samebug.clients.idea.ui.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.search.api.entities.StackTraceSearch;
import com.samebug.clients.search.api.entities.MarkResponse;
import com.samebug.clients.search.api.entities.RestHit;
import com.samebug.clients.search.api.exceptions.BadRequest;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

final public class MarkHandler extends MouseAdapter {
    @NotNull
    final Project project;
    @NotNull
    final StackTraceSearch search;
    @NotNull
    final RestHit hit;
    @NotNull
    final MarkPanel markPanel;

    public MarkHandler(@NotNull Project project, @NotNull StackTraceSearch search, @NotNull RestHit hit, @NotNull MarkPanel markPanel) {
        this.project = project;
        this.search = search;
        this.hit = hit;
        this.markPanel = markPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        markPanel.beginPostMark();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    IdeaClientService client = IdeaSamebugPlugin.getInstance().getClient();
                    if (hit.markId == null) {
                        final MarkResponse mark = client.postMark(search.id, hit.solutionId);
                        hit.markId = mark.id;
                        hit.score = mark.marks;
                    } else {
                        final MarkResponse mark = client.retractMark(hit.markId);
                        hit.markId = null;
                        hit.score = mark.marks;
                    }
                    Tracking.projectTracking(project).trace(
                            Events.markSubmit(project, search.id, hit.solutionId, hit.markId == null ? "null" : hit.markId.toString()));
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            markPanel.finishPostMarkWithSuccess();
                        }
                    });
                } catch (final BadRequest e) {
                    final String errorMessageKey;
                    final String markErrorCode = e.getRestError().code;
                    if ("ALREADY_MARKED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyMarked";
                    else if ("NOT_YOUR_SEARCH".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourSearch";
                    else if ("NOT_YOUR_MARK".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourMark";
                    else if ("ALREADY_CANCELLED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyCancelled";
                    else errorMessageKey = "samebug.mark.error.unhandledBadRequest";
                    Tracking.projectTracking(project).trace(
                            Events.markSubmit(project, search.id, hit.solutionId, errorMessageKey));
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            markPanel.finishPostMarkWithError(SamebugBundle.message(errorMessageKey));
                        }
                    });

                } catch (final SamebugClientException e) {
                    Tracking.projectTracking(project).trace(
                            Events.markSubmit(project, search.id, hit.solutionId, "samebug.mark.error.unhandled"));
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            markPanel.finishPostMarkWithError(SamebugBundle.message("samebug.mark.error.unhandled"));
                        }
                    });
                }
            }
        });

    }
}
