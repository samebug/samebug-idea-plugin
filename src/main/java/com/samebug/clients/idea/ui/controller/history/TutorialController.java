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
package com.samebug.clients.idea.ui.controller.history;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.project.TutorialProjectComponent;
import com.samebug.clients.idea.messages.view.HistoryViewListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.TutorialPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

final class TutorialController implements HistoryViewListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final HistoryTabController controller;

    public TutorialController(@NotNull final HistoryTabController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(HistoryViewListener.TOPIC, this);
    }

    @Override
    public void setRecurringFilter(boolean on) {
        TutorialProjectComponent.withTutorialProject(controller.myProject, new HideRecurringSearchesTutorial(on));
    }

    @Override
    public void setZeroSolutionFilter(boolean on) {

    }

    @Override
    public void reloadHistory() {

    }

    // TODO it would be nice to not subscribe when it is already triggered.
    class HideRecurringSearchesTutorial extends TutorialProjectComponent.TutorialProjectAnonfun<Void> {
        final boolean showRecurringSearches;

        public HideRecurringSearchesTutorial(boolean showRecurringSearches) {
            this.showRecurringSearches = showRecurringSearches;
        }

        @Override
        public Void call() {
            if (!showRecurringSearches && settings.recurringExceptionsFilter) {
                settings.recurringExceptionsFilter = false;
                final JPanel tutorialPanel = new TutorialPanel(SamebugBundle.message("samebug.tutorial.recurringExceptionsFilter.title"),
                        SamebugBundle.message("samebug.tutorial.recurringExceptionsFilter.message"));
                Balloon balloon = TutorialProjectComponent.createTutorialBalloon(project, tutorialPanel);
                balloon.show(RelativePoint.getNorthWestOf(controller.view.toolbarPanel), Balloon.Position.above);
            }
            return null;
        }
    }

}
