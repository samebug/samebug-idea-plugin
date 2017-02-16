package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.AvatarIcon;
import com.samebug.clients.idea.ui.component.util.SamebugLabel;
import com.samebug.clients.idea.ui.component.util.SamebugMultiLineLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Date;

public final class TipHit extends JPanel {
    private final Model model;
    private final MessageBus messageBus;

    private final SamebugLabel tipLabel;
    private final MessageLabel tipMessage;
    private final MarkPanel mark;

    public TipHit(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        tipLabel = new SamebugLabel("TIP", FontRegistry.AvenirRegular, 14);
        tipMessage = new MessageLabel();
        mark = new MarkPanel(messageBus, model.mark);
        final JPanel filler = new JPanel() {
            {
                setOpaque(false);
            }
        };
        final AuthorPanel author = new AuthorPanel();

        setOpaque(false);
        setLayout(new MigLayout("fillx", "20[fill, 300]20", "20[]15[]15[]20"));

        add(tipLabel, "cell 0 0");
        add(tipMessage, "cell 0 1, wmin 0, growx");
        add(mark, "cell 0 2, align left");
        add(filler, "cell 0 2, growx");
        add(author, "cell 0 2, align right");
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (tipLabel != null) tipLabel.setForeground(ColorUtil.tipText());
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = DrawUtil.init(g);
        g2.setColor(ColorUtil.tip());
        g2.fillRoundRect(0,0, getWidth(), getHeight(), 5, 5);
    }

    private final class MessageLabel extends SamebugMultiLineLabel {
        {
            setText(TipHit.this.model.message);
        }

        @Override
        public void updateUI() {
            super.updateUI();
            setForeground(ColorUtil.tipText());
        }
    }

    private final class AuthorPanel extends JPanel {
        private final static int AvatarIconSize = 26;
        private final SamebugLabel name;
        private final SamebugLabel timestamp;

        {
            final AvatarIcon authorIcon = new AvatarIcon(model.createdByAvatarUrl, AvatarIconSize);
            name = new SamebugLabel(model.createdBy, FontRegistry.AvenirRegular, 12);
            timestamp = new SamebugLabel(TextUtil.prettyTime(model.createdAt), FontRegistry.AvenirRegular, 12);

            setOpaque(false);
            setLayout(new MigLayout("", "0[]5[]0", "0[14!]0[14!]0"));

            add(authorIcon, "cell 0 0, spany 2");
            add(name, "cell 1 0");
            add(timestamp, "cell 1 1");
        }

        // TODO this is really error prone. Handling updateUI this way is bad
        @Override
        public void updateUI() {
            super.updateUI();
            if (name != null && timestamp != null) {
                name.setForeground(ColorUtil.unemphasizedText());
                timestamp.setForeground(ColorUtil.unemphasizedText());
            }
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
