package com.samebug.clients.idea.tracking;

import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;

import javax.swing.*;

class ShowToolwindowBuilder extends TrackBuilder {
    public ShowToolwindowBuilder(String category, String action, BaseFrameController controller) {
        super(category, action);
        try {
            JComponent v = (JComponent) controller.view;
            add("screenWidth", v.getWidth());
            add("screenHeight", v.getHeight());
        } catch (Exception ignored) {
        }
    }
}
