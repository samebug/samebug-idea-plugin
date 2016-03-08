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
import com.intellij.openapi.project.DumbAware;
import com.samebug.clients.idea.messages.HistoryListener;

/**
 * Created by poroszd on 3/7/16.
 */
public class ShowOld extends ToggleAction implements DumbAware {
    private boolean selected = false;

    @Override
    public boolean isSelected(AnActionEvent e) {
        return selected;
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        selected = state;
        e.getProject().getMessageBus().syncPublisher(HistoryListener.UPDATE_HISTORY_TOPIC).toggleShowOldSearches(state);
    }
}
