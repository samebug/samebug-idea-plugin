package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.IHelpOthersCTA;
import com.samebug.clients.idea.ui.SamebugBundle;
import com.samebug.clients.idea.ui.component.util.button.SamebugButton;
import com.samebug.clients.idea.ui.component.util.multiline.CenteredMultilineLabel;
import com.samebug.clients.idea.ui.component.util.panel.EmphasizedPanel;
import net.miginfocom.swing.MigLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LargeWriteTipCTA extends EmphasizedPanel implements IHelpOthersCTA {
    protected final Model model;
    protected final MessageBus messageBus;

    protected final CenteredMultilineLabel label;

    public LargeWriteTipCTA(MessageBus messageBus, Model model) {
        this.messageBus = messageBus;
        this.model = new Model(model);

        final SamebugButton button = new SamebugButton(SamebugBundle.message("samebug.component.tip.write.cta.button"), true);
        label = new CenteredMultilineLabel();

        setLayout(new MigLayout("fillx, w 300", "40[]40", "40[]20[]40"));
        add(label, "cell 0 0, wmin 0, growx");
        add(button, "cell 0 1, align center");

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getListener().ctaClicked(LargeWriteTipCTA.this);
            }
        });
    }

    private Listener getListener() {
        return messageBus.syncPublisher(Listener.TOPIC);
    }
}
