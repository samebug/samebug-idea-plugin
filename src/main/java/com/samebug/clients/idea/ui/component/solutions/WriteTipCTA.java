package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.util.button.SamebugButton;
import com.samebug.clients.idea.ui.component.util.multiline.SamebugMultiLineLabel;
import net.miginfocom.swing.MigLayout;

public final class WriteTipCTA extends HelpOthersCTA {
    private final HelpOthersCTA.Model model;
    private final MessageBus messageBus;

    public WriteTipCTA(MessageBus messageBus, HelpOthersCTA.Model model) {
        this.messageBus = messageBus;
        this.model = new HelpOthersCTA.Model(model);

        final SamebugButton button = new SamebugButton();
        button.setText(SamebugBundle.message("samebug.component.tip.write.cta.button"));
        final SamebugMultiLineLabel label = new SamebugMultiLineLabel();
        label.setText(SamebugBundle.message("samebug.component.cta.writeTip.tips.label", model.usersWaitingHelp));

        setLayout(new MigLayout("fillx, w 300", "20[fill]50[fill]10", "20[fill]20"));
        add(button, "cell 0 0");
        add(label, "cell 1 0, wmin 0");
    }
}
