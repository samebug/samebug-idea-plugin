/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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
import com.samebug.clients.idea.messages.HistoryListener;
import com.samebug.clients.idea.ui.controller.HistoryTabController;

/**
 * Created by poroszd on 3/7/16.
 */
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
            ServiceManager.getService(e.getProject(), HistoryTabController.class).setShowRecurringSearches(state);
            e.getProject().getMessageBus().syncPublisher(HistoryListener.UPDATE_HISTORY_TOPIC).toggleShowOldSearches(state);
        }
    }
}
