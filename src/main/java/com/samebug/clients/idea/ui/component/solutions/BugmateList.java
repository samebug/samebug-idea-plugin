package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.button.SamebugButton;
import com.samebug.clients.idea.ui.component.util.label.SamebugLabel;
import com.samebug.clients.idea.ui.component.util.panel.TransparentPanel;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class BugmateList extends TransparentPanel {
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
                setLayout(new MigLayout("fillx", "0[]20[]0", "0[]0"));
            } else {
                setLayout(new MigLayout("fillx", "0[]20[]0", "0[]20[]0"));
            }

            for (int i = 0; i < bugmateHits.size(); ++i) {
                BugmateHit hit = bugmateHits.get(i);
                if (i % 2 == 0) add(hit, "align left");
                else if (i == bugmateHits.size() - 1) add(hit, "align right");
                else add(hit, "align right, wrap");
            }
        }
    }

    private final class SubheaderLabel extends SamebugLabel {
        {
            setText(SamebugBundle.message("samebug.component.bugmate.list.title"));
            setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 16));
        }
    }

    private final class MoreLabel extends SamebugLabel {
        {
            setText(SamebugBundle.message("samebug.component.bugmate.list.more", model.numberOfOtherBugmates));
            setFont(new Font(FontRegistry.AvenirRegular, Font.PLAIN, 14));
        }
    }

    private final class AskButton extends SamebugButton {
        {
            setText(SamebugBundle.message("samebug.component.bugmate.list.ask"));
        }
    }

    public static final class Model {
        private final List<BugmateHit.Model> bugmateHits;
        private final int numberOfOtherBugmates;
        private final boolean evenMoreExists;

        public Model(Model rhs) {
            this(rhs.bugmateHits, rhs.numberOfOtherBugmates, rhs.evenMoreExists);
        }

        public Model(List<BugmateHit.Model> bugmateHits, int numberOfOtherBugmates, boolean evenMoreExists) {
            this.bugmateHits = bugmateHits;
            this.numberOfOtherBugmates = numberOfOtherBugmates;
            this.evenMoreExists = evenMoreExists;
        }
    }
}
