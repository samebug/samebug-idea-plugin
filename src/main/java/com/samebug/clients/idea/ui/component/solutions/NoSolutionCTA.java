package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.util.button.SamebugButton;
import com.samebug.clients.idea.ui.component.util.multiline.CenteredMultilineLabel;
import net.miginfocom.swing.MigLayout;

public final class NoSolutionCTA extends HelpOthersCTA {
    private final HelpOthersCTA.Model model;
    private final MessageBus messageBus;

    private final CenteredMultilineLabel label;

    public NoSolutionCTA(MessageBus messageBus, HelpOthersCTA.Model model) {
        this.messageBus = messageBus;
        this.model = new HelpOthersCTA.Model(model);

        final SamebugButton button = new SamebugButton(SamebugBundle.message("samebug.component.tip.write.cta.button"), true);
        label = new CenteredMultilineLabel();

        setLayout(new MigLayout("fillx, w 300", "40[]40", "40[]20[]40"));
        add(label, "cell 0 0, wmin 0, growx");
        add(button, "cell 0 1, align center");
    }

    // TODO this is not a good way to reuse this component
    public void setTextForSolutions() {
        label.setText(SamebugBundle.message("samebug.component.cta.writeTip.noWebHits.label", model.usersWaitingHelp));
    }

    // TODO this is not a good way to reuse this component
    public void setTextForTips() {
        label.setText(SamebugBundle.message("samebug.component.cta.writeTip.noTipHits.label", model.usersWaitingHelp));
    }
}
