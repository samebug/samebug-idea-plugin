package com.samebug.clients.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import com.samebug.clients.idea.messages.HistoryListener;

/**
 * Created by poroszd on 3/7/16.
 */
public class ShowZeroSolution extends ToggleAction implements DumbAware {
    private boolean selected = false;

    @Override
    public boolean isSelected(AnActionEvent e) {
        return selected;
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        selected = state;
        e.getProject().getMessageBus().syncPublisher(HistoryListener.UPDATE_HISTORY_TOPIC).toggleShowSearchedWithZeroSolution(state);
    }
}
