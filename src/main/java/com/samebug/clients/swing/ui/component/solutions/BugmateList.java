package com.samebug.clients.swing.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.IBugmateList;
import com.samebug.clients.swing.ui.FontRegistry;
import com.samebug.clients.swing.ui.SamebugBundle;
import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import net.miginfocom.swing.MigLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class BugmateList extends TransparentPanel implements IBugmateList {
    private final Model model;
    private final MessageBus messageBus;

    public BugmateList(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        final SubheaderLabel subheader = new SubheaderLabel();
        final BugmateGrid bugmateGrid = new BugmateGrid();
        final MoreLabel more = new MoreLabel();
        final AskButton askButton = new AskButton();

        setLayout(new MigLayout("fillx", "0[]0", "0[]25[]25[]10[]0"));

        add(subheader, "cell 0 0");
        add(bugmateGrid, "cell 0 1, growx");
        add(more, "cell 0 2, align center");
        add(askButton, "cell 0 3, align center");
    }

    private final class BugmateGrid extends TransparentPanel {
        private final List<BugmateHit> bugmateHits;

        {
            bugmateHits = new ArrayList<BugmateHit>(model.bugmateHits.size());
            for (int i = 0; i < model.bugmateHits.size(); ++i) {
                BugmateHit hit = new BugmateHit(messageBus, model.bugmateHits.get(i));
                bugmateHits.add(hit);
            }

            // TODO generalize it if necessary, for 4 items it's fine
            if (bugmateHits.size() <= 2) {
                setLayout(new MigLayout("fillx", "0[]20:push[]0", "0[]0"));
            } else {
                setLayout(new MigLayout("fillx", "0[]20:push[]0", "0[]20[]0"));
            }

            for (int i = 0; i < bugmateHits.size(); ++i) {
                BugmateHit hit = bugmateHits.get(i);
                if (i % 2 == 0) add(hit, "");
                else if (i == bugmateHits.size() - 1) add(hit, "");
                else add(hit, "wrap");
            }
        }
    }

    private final class SubheaderLabel extends SamebugLabel {
        {
            setText(SamebugBundle.message("samebug.component.bugmate.list.title"));
            setFont(FontRegistry.demi(16));
        }
    }

    private final class MoreLabel extends SamebugLabel {
        {
            setText(SamebugBundle.message("samebug.component.bugmate.list.more", model.numberOfOtherBugmates));
            setFont(FontRegistry.regular(14));
        }
    }

    private final class AskButton extends SamebugButton {
        {
            setText(SamebugBundle.message("samebug.component.bugmate.list.ask"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().askBugmates(BugmateList.this);
                }
            });
        }
    }

    private Listener getListener() {
        return messageBus.syncPublisher(Listener.TOPIC);
    }
}
