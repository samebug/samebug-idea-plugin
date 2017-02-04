package com.samebug.clients.idea.ui.component.experimental;

import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class WebHit extends JPanel {
    TitlePanel titlePanel;
    MultiLineLabel preview;
    MarkPanel markPanel;

    public WebHit() {
        markPanel = new MarkPanel();
        titlePanel = new TitlePanel();
        preview = new MultiLineLabel("CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0") {
            {
                setForeground(Constants.TextColor);
                setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 16));
            }
        };

        setBackground(Color.white);
        setLayout(new MigLayout(
                "fillx",
                "0[300]0",
                "30[]16[]17[]30"));

        add(titlePanel, "growx, cell 0 0");
        add(preview, "growx, cell 0 1, wmin 100");
        add(markPanel, "cell 0 2");
    }
}

final class TitlePanel extends JPanel {
    SourceIcon sourceIcon;
    JComponent title;
    JLabel source;

    // TODO dirty testing var
    static int i = 0;

    public TitlePanel() {
        sourceIcon = new SourceIcon();
        String titleText;
        if (i == 0) titleText = "Android rawQuery result check";
        else titleText = "FATAL EXCEPTION: main and java.lang.RuntimeException: Unable to start activity";
        i++;
        title = new MultiLineLabel(titleText) {
            {
                setForeground(Constants.TextColor);
                final Font font = new Font(Constants.AvenirRegular, Font.PLAIN, 16);
                Map attributes = font.getAttributes();
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                setFont(font.deriveFont(attributes));
            }

            @Override
            public Dimension getPreferredSize() {
                if (getLineCount() <= 1) {
                    return new Dimension(Integer.MAX_VALUE, 18);
                } else {
                    return new Dimension(Integer.MAX_VALUE, 40);
                }
            }
        };

        source = new JLabel("Stack Overflow by Dimitrov, 1 year ago") {
            {
                setForeground(Constants.UnemphasizedTextColor);
                setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 12));
            }
        };

        setLayout(new MigLayout(
                "",
                "0[]9[]0",
                "0[]0[]0"
        ));
        setOpaque(false);
        add(sourceIcon, "w 40!, h 40!, cell 0 0, span 1 2, ay top");
        add(title, "wmin 0, hmax 40, growx, cell 1 0");
        add(source, "wmin 0, growx, cell 1 1");
    }
}

final class SourceIcon extends JPanel {
    final Image sourceIcon;
    static final int width = 40;
    static final int height = 40;

    public SourceIcon() {
        final URL sourceIconUrl = this.getClass().getClassLoader().getResource("com/samebug/swing/plugin/sources/stackoverflow.png");
        Image tmp;
        try {
            Image rawSouceIcon = ImageIO.read(sourceIconUrl);
            tmp = rawSouceIcon.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (Throwable e) {
            e.printStackTrace();
            tmp = null;
        }
        sourceIcon = tmp;
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(sourceIcon, 0, 0, null, null);
    }
}
