package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.AvatarIcon;
import com.samebug.clients.idea.ui.component.util.SamebugLabel;
import com.samebug.clients.idea.ui.component.util.SamebugMultiLineLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.net.URL;
import java.util.Date;

public final class TipHit extends JPanel {
    private final Model model;
    private final MessageBus messageBus;

    private final MarkPanel mark;

    public TipHit(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        final SamebugLabel tipLabel = new SamebugLabel("TIP", FontRegistry.AvenirRegular, 14);
        tipLabel.setForeground(ColorUtil.tipText());
        final MessageLabel tipMessage = new MessageLabel();
        mark = new MarkPanel(messageBus, model.mark);
        final JPanel filler = new JPanel() {
            {
                setOpaque(false);
            }
        };
        final AuthorPanel author = new AuthorPanel();

        setLayout(new MigLayout("fillx", "20[fill, 300]20", "20[]15[]15[]20"));
        setBackground(ColorUtil.tip());

        add(tipLabel, "cell 0 0");
        add(tipMessage, "cell 0 1, wmin 0, growx");
        add(mark, "cell 0 2, align left");
        add(filler, "cell 0 2, growx");
        add(author, "cell 0 2, align right");
    }

    private final class MessageLabel extends SamebugMultiLineLabel {
        {
            setText(TipHit.this.model.message);
            setForeground(ColorUtil.tipText());
        }
    }

    private final class AuthorPanel extends JPanel {
        private final static int AvatarIconSize = 26;

        {
            final AvatarIcon authorIcon = new AvatarIcon(model.createdByAvatarUrl, AvatarIconSize);
            final SamebugLabel name = new SamebugLabel(model.createdBy, FontRegistry.AvenirRegular, 12);
            final SamebugLabel timestamp = new SamebugLabel(TextUtil.prettyTime(model.createdAt), FontRegistry.AvenirRegular, 12);

            name.setForeground(ColorUtil.unemphasizedText());
            timestamp.setForeground(ColorUtil.unemphasizedText());

            setOpaque(false);
            setLayout(new MigLayout("", "0[]5[]0", "0[]0[]0"));

            add(authorIcon, "cell 0 0, spany 2");
            add(name, "cell 1 0");
            add(timestamp, "cell 1 1");
        }
    }

    public static final class Model {
        private final String message;
        private final Date createdAt;
        private final String createdBy;
        private final URL createdByAvatarUrl;
        private final MarkPanel.Model mark;

        public Model(Model rhs) {
            this(rhs.message, rhs.createdAt, rhs.createdBy, rhs.createdByAvatarUrl, rhs.mark);
        }

        public Model(String message, Date createdAt, String createdBy, URL createdByAvatarUrl, MarkPanel.Model mark) {
            this.message = message;
            this.createdAt = createdAt;
            this.createdBy = createdBy;
            this.createdByAvatarUrl = createdByAvatarUrl;
            this.mark = mark;
        }
    }
}
