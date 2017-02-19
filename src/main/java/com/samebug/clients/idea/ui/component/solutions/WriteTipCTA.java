package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.IHelpOthersCTA;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.component.util.button.SamebugButton;
import com.samebug.clients.idea.ui.component.util.multiline.SamebugMultilineLabel;
import net.miginfocom.swing.MigLayout;

public final class WriteTipCTA extends TransparentPanel implements IHelpOthersCTA {
    private final IHelpOthersCTA.Model model;
    private final MessageBus messageBus;

    public WriteTipCTA(MessageBus messageBus, IHelpOthersCTA.Model model) {
        this.messageBus = messageBus;
        this.model = new IHelpOthersCTA.Model(model);

        final SamebugButton button = new SamebugButton();
        button.setText(SamebugBundle.message("samebug.component.tip.write.cta.button"));
        final SamebugMultilineLabel label = new SamebugMultilineLabel();
        label.setText(SamebugBundle.message("samebug.component.cta.writeTip.tips.label", model.usersWaitingHelp));

        setLayout(new MigLayout("fillx, w 300", "20[fill]50[fill]10", "20[fill]20"));
        add(button, "cell 0 0");
        add(label, "cell 1 0, wmin 0");
    }
}
