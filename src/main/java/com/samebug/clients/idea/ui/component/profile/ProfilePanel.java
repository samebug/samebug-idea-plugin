package com.samebug.clients.idea.ui.component.profile;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.AvatarIcon;
import com.samebug.clients.idea.ui.component.util.label.Label;
import com.samebug.clients.idea.ui.component.util.label.LinkLabel;
import com.samebug.clients.idea.ui.component.util.label.SecondaryLinkLabel;
import com.samebug.clients.idea.ui.component.util.panel.TransparentPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class ProfilePanel extends TransparentPanel {
    private final static int AvatarIconSize = 26;

    private final Model model;
    private final MessageBus messageBus;

    private final AvatarIcon avatarIcon;
    private final Label messages;
    private final Label marks;
    private final Label tips;
    private final Label thanks;

    public ProfilePanel(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        avatarIcon = new AvatarIcon(model.avatarUrl, AvatarIconSize);
        Label name = new Label(model.name, FontRegistry.AvenirDemi, 14);
        final JPanel glue = new TransparentPanel();
        messages = new Label(Integer.toString(model.messages), FontRegistry.AvenirDemi, 14);
        final JLabel messagesHint = new SecondaryLinkLabel(SamebugBundle.message("samebug.component.profile.messages.label"), FontRegistry.AvenirDemi, 12);
        marks = new Label(Integer.toString(model.marks), FontRegistry.AvenirRegular, 14);
        final Label marksHint = new Label(SamebugBundle.message("samebug.component.profile.marks.label"), FontRegistry.AvenirRegular, 12);
        tips = new Label(Integer.toString(model.tips), FontRegistry.AvenirRegular, 14);
        final Label tipsHint = new Label(SamebugBundle.message("samebug.component.profile.tips.label"), FontRegistry.AvenirRegular, 12);
        thanks = new Label(Integer.toString(model.thanks), FontRegistry.AvenirRegular, 14);
        final Label thanksHint = new Label(SamebugBundle.message("samebug.component.profile.thanks.label"), FontRegistry.AvenirRegular, 12);

        setLayout(new MigLayout("fillx", "0[]8[]0[grow]0[]4[]19[]4[]19[]4[]19[]4[]0", "10[]10"));

        add(avatarIcon, "");
        add(name, "");
        add(glue, "");
        add(messages, "");
        add(messagesHint, "");
        add(marks, "");
        add(marksHint, "");
        add(tips, "");
        add(tipsHint, "");
        add(thanks, "");
        add(thanksHint, "");
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // Border is set here so the color will be updated on theme change
        Color borderColor = ColorUtil.forCurrentTheme(ColorUtil.Separator);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));
    }

    public static final class Model {
        private final int messages;
        private final int marks;
        private final int tips;
        private final int thanks;
        private final String name;
        private final URL avatarUrl;

        public Model(Model rhs) {
            this(rhs.messages, rhs.marks, rhs.tips, rhs.thanks, rhs.name, rhs.avatarUrl);
        }

        public Model(int messages, int marks, int tips, int thanks, String name, URL avatarUrl) {
            this.messages = messages;
            this.marks = marks;
            this.tips = tips;
            this.thanks = thanks;
            this.name = name;
            this.avatarUrl = avatarUrl;
        }
    }
}

