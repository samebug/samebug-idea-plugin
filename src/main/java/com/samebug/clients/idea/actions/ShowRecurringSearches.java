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
package com.samebug.clients.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.ui.awt.RelativePoint;
import com.samebug.clients.idea.components.project.TutorialProjectComponent;
import com.samebug.clients.idea.messages.HistoryListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.TutorialPanel;
import com.samebug.clients.idea.ui.controller.HistoryTabController;

import javax.swing.*;

public class ShowRecurringSearches extends ToggleAction implements DumbAware {
    @Override
    public boolean isSelected(AnActionEvent e) {
        if (e.getProject() != null) {
            return ServiceManager.getService(e.getProject(), HistoryTabController.class).isShowRecurringSearches();
        } else {
            return false;
        }
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        if (e.getProject() != null) {
            final HistoryTabController historyTab = ServiceManager.getService(e.getProject(), HistoryTabController.class);
            historyTab.setShowRecurringSearches(state);
            e.getProject().getMessageBus().syncPublisher(HistoryListener.UPDATE_HISTORY_TOPIC).toggleShowOldSearches(state);


            TutorialProjectComponent.withTutorialProject(e.getProject(), new TutorialProjectComponent.TutorialProjectAnonfun<Void>() {
                @Override
                public Void call() {
                    if (settings.recurringExceptionsFilter) {
                        settings.recurringExceptionsFilter = false;
                        final JPanel tutorialPanel = new TutorialPanel(SamebugBundle.message("samebug.tutorial.recurringExceptionsFilter.title"),
                                SamebugBundle.message("samebug.tutorial.recurringExceptionsFilter.message"));
                        Balloon balloon = TutorialProjectComponent.createTutorialBalloon(project, tutorialPanel);
                        balloon.show(RelativePoint.getNorthWestOf(historyTab.view.toolbarPanel), Balloon.Position.above);
                    }
                    return null;
                }
            });
        }
    }
}
