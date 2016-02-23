package com.samebug.clients.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.samebug.clients.idea.ui.SamebugHistoryWindow;

/**
 * Created by poroszd on 2/23/16.
 */
public class RecentFilterAction extends AnAction {
    private SamebugHistoryWindow historyWindow;

    // TODO disable action when user is not logged in

    @Override
    public void actionPerformed(AnActionEvent e) {
        historyWindow.setRecentFilterOn(!historyWindow.isRecentFilterOn());
    }

    @Override
    public void update(AnActionEvent e) {
        if (historyWindow == null) {
            e.getPresentation().setEnabled(false);
        } else if (historyWindow.isRecentFilterOn()) {
            e.getPresentation().setEnabled(true);
            e.getPresentation().setText("recent filter on");
        } else {
            e.getPresentation().setEnabled(true);
            e.getPresentation().setText("recent filter off");
        }
    }

    public void setHook(SamebugHistoryWindow historyWindow) {
        this.historyWindow = historyWindow;
    }
}
