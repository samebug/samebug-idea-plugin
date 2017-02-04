package com.samebug.clients.idea.ui.component.experimental;

import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public final class ProfilePanel extends JPanel {
    public final static int AvatarIconSize = 26;

    final JPanel avatarIcon;
    final JLabel name;
    final JComponent glue;
    final JLabel messages;
    final JLabel messagesHint;
    final JLabel marks;
    final JLabel marksHint;
    final JLabel tips;
    final JLabel tipsHint;
    final JLabel thanks;
    final JLabel thanksHint;

    Model model;

    public ProfilePanel(Model model) {
        this.model = new Model(model);
        avatarIcon = new AvatarIcon(null);
        name = new SamebugLabel("David", Constants.AvenirDemi, 14);
        glue = new JPanel() {
            {
                setOpaque(false);
            }
        };
        messages = new SamebugLabel("2", Constants.AvenirDemi, 14);
        messagesHint = new SamebugLabel("Messages", Constants.AvenirDemi, 12);
        marks = new SamebugLabel("16", Constants.AvenirRegular, 14);
        marksHint = new SamebugLabel("Marks", Constants.AvenirRegular, 12);
        tips = new SamebugLabel("48", Constants.AvenirRegular, 14);
        tipsHint = new SamebugLabel("Tips", Constants.AvenirRegular, 12);
        thanks = new SamebugLabel("15", Constants.AvenirRegular, 14);
        thanksHint = new SamebugLabel("Thanks", Constants.AvenirRegular, 12);

        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.SeparatorColor),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));
        setLayout(new MigLayout(
                "fillx",
                "0[]8[]0[grow]0[]4[]19[]4[]19[]4[]19[]4[]0",
                "10[]10"
        ));

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

    public void updateMessages() {}
    public void updateMarks() {}
    public void updateTips() {}
    public void updateThanks() {}
    public void updateProfile() {}

    public static class Model {
        public int messages;
        public int marks;
        public int tips;
        public int thanks;
        public String name;
        public URL avatarUrl;

        public Model(Model rhs) {
           // this(rhs.messages, rhs.marks, rhs.tips, rhs.thanks, rhs.name, rhs.avatarUrl);
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

final class AvatarIcon extends JPanel {
    final Image avatar;

    public AvatarIcon(URL avatarUrl) {
        Image tmpAvatar = null;
        try {
            Image rawAvatar = ImageIO.read(avatarUrl);
            tmpAvatar = rawAvatar.getScaledInstance(ProfilePanel.AvatarIconSize, ProfilePanel.AvatarIconSize, Image.SCALE_SMOOTH);

        } catch (Throwable e) {
//            e.printStackTrace();
        }
        this.avatar = tmpAvatar;
        setPreferredSize(new Dimension(ProfilePanel.AvatarIconSize, ProfilePanel.AvatarIconSize));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(avatar, 0, 0, null, null);
    }
}

final class SamebugLabel extends JLabel {
    public SamebugLabel(String text, String fontName, int fontSize) {
        super(text);
        setForeground(Constants.TextColor);
        setFont(new Font(fontName, Font.PLAIN, fontSize));
    }
}
