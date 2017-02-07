package com.samebug.clients.idea.ui.component.experimental;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.ui.ImageUtil;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.util.Date;
import java.util.Map;

public class WebHit extends JPanel {
    Model model;

    @NotNull
    final TitlePanel titlePanel;
    @NotNull
    final PreviewPanel preview;
    @NotNull
    final MarkPanel markPanel;
    @NotNull
    final MessageBus messageBus;

    public WebHit(MessageBus messageBus, Model model) {
        markPanel = new MarkPanel(messageBus, model.mark);
        titlePanel = new TitlePanel();
        preview = new PreviewPanel();
        this.messageBus = messageBus;

        setBackground(Color.white);
        setLayout(new MigLayout("fillx", "0[300]0", "30[]16[]17[]30"));

        add(titlePanel, "growx, cell 0 0");
        add(preview, "growx, cell 0 1, wmin 100");
        add(markPanel, "cell 0 2");

        update(model);
    }

    public void update(Model model) {
        this.model = model;

        titlePanel.update(model);
        preview.update(model);
        markPanel.update(model.mark);
    }


    public static final class Model {
        public String title;
        public URL url;
        public Date createdAt;
        public String sourceName;
        public URL sourceIconUrl;
        public MarkPanel.Model mark;

        public Model(Model rhs) {
            this(rhs.title, rhs.url, rhs.createdAt, rhs.sourceName, rhs.sourceIconUrl, rhs.mark);
        }

        public Model(String title, URL url, Date createdAt, String sourceName, URL sourceIconUrl, MarkPanel.Model mark) {
            this.title = title;
            this.url = url;
            this.createdAt = createdAt;
            this.sourceName = sourceName;
            this.sourceIconUrl = sourceIconUrl;
            this.mark = mark;
        }
    }
}

final class TitlePanel extends JPanel {
    SourceIcon sourceIcon;
    TitleLabel title;
    SourceLabel source;

    public TitlePanel() {
        sourceIcon = new SourceIcon();
        title = new TitleLabel();
        source = new SourceLabel();

        setOpaque(false);
        setLayout(new MigLayout("", "0[]9[]0", "0[]0[]0"));
        add(sourceIcon, "w 40!, h 40!, cell 0 0, span 1 2, ay top");
        add(title, "wmin 0, hmax 40, growx, cell 1 0");
        add(source, "wmin 0, growx, cell 1 1");
    }

    public void update(WebHit.Model model) {
        String sourceText = "" + model.sourceName + " by " + "xxx" + " | " + String.format("%s", TextUtil.prettyTime(model.createdAt));
        title.setText(model.title);
        source.setText(sourceText);
    }
}

final class TitleLabel extends MultiLineLabel {
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
}

final class SourceLabel extends JLabel {
    {
        setForeground(Constants.UnemphasizedTextColor);
        setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 12));
    }
}

final class SourceIcon extends JPanel {
    Image sourceIcon;
    static final int width = 40;
    static final int height = 40;

    {
        setOpaque(false);
    }

    public void update(WebHit.Model model) {
        sourceIcon = ImageUtil.getScaled(model.sourceIconUrl, width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(sourceIcon, 0, 0, null, null);
    }
}

final class PreviewPanel extends MultiLineLabel {
    {
        setForeground(Constants.TextColor);
        setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 16));
    }

    public void update(WebHit.Model model) {
        setText("bla bla");
    }
}