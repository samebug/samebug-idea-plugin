package com.samebug.clients.idea.ui.component.card;

import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class HitView extends JPanel {
    public final MarkPanel markPanel;

    public HitView(@NotNull MarkPanel.Model model) {
        markPanel = new MarkPanel(model);
    }
}
