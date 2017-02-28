package com.samebug.clients.swing.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.common.ui.component.solutions.IBugmateHit;
import com.samebug.clients.swing.ui.FontRegistry;
import com.samebug.clients.swing.ui.SamebugBundle;
import com.samebug.clients.swing.ui.component.util.AvatarIcon;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import net.miginfocom.swing.MigLayout;

public final class BugmateHit extends TransparentPanel implements IBugmateHit {
    private final static int AvatarSize = 44;

    private final Model model;
    private final MessageBus messageBus;

    public BugmateHit(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        final NameLabel name = new NameLabel();
        final TimestampLabel timestamp = new TimestampLabel();
        final AvatarIcon avatar = new AvatarIcon(model.avatarUrl, AvatarSize);

        setLayout(new MigLayout("", "0[]10[]0", "0[]0[]0"));

        add(avatar, "cell 0 0, spany 2");
        add(name, "cell 1 0");
        add(timestamp, "cell 1 1");
    }


    private final class NameLabel extends SamebugLabel {
        {
            setText(model.displayName);
            setFont(FontRegistry.demi(14));
        }
    }

    private final class TimestampLabel extends SamebugLabel {
        {
            setText(SamebugBundle.message("samebug.component.bugmate.hit.occurred", model.nSeen, TextUtil.prettyTime(model.lastSeen)));
            setFont(FontRegistry.regular(14));
        }
    }
}
