package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.component.util.SamebugMultiLineLabel;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;

public final class WebHit extends JPanel implements DataProvider {
    private final Model model;
    private final MessageBus messageBus;

    private final MarkPanel markPanel;

    public WebHit(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        markPanel = new MarkPanel(messageBus, model.mark);
        final TitlePanel titlePanel = new TitlePanel();

        setLayout(new MigLayout("fillx", "0[300]0", "0[]16[]0"));

        add(titlePanel, "growx, cell 0 0");
        add(markPanel, "cell 0 1");
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(ColorUtil.background());
    }
    private final class TitlePanel extends JPanel {
        private final static int Size = 40;

        {
            final SourceIcon sourceIcon = new SourceIcon();
            final TitleLabel title = new TitleLabel();
            final SourceLabel source = new SourceLabel();

            setOpaque(false);
            setLayout(new MigLayout("", "0[]9[]0", "0[]0[]0"));
            add(sourceIcon, MessageFormat.format("w {0}!, h {0}!, cell 0 0, span 1 2, ay top", Size));
            add(title, MessageFormat.format("wmin 0, hmax {0}, growx, cell 1 0", Size));
            add(source, "wmin 0, growx, cell 1 1");
        }
    }

    private final class TitleLabel extends SamebugMultiLineLabel {
        {
            setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 16));
            setText(model.title);
        }

        @Override
        public void updateUI() {
            super.updateUI();
            setForeground(ColorUtil.samebug());
        }
        @Override
        public Dimension getPreferredSize() {
            // TODO breaks when changing font
            if (getLineCount() <= 1) {
                return new Dimension(Integer.MAX_VALUE, 18);
            } else {
                return new Dimension(Integer.MAX_VALUE, TitlePanel.Size);
            }
        }
    }

    private final class SourceLabel extends JLabel {
        {
            setFont(new Font(FontRegistry.AvenirRegular, Font.PLAIN, 12));
            String sourceText;
            if (model.createdBy == null) {
                sourceText = model.sourceName + " | " + String.format("%s", TextUtil.prettyTime(model.createdAt));
            } else {
                sourceText = model.sourceName + " by " + model.createdBy + " | " + String.format("%s", TextUtil.prettyTime(model.createdAt));
            }
            setText(sourceText);
        }
        @Override
        public void updateUI() {
            super.updateUI();
            setForeground(ColorUtil.unemphasizedText());
        }
    }

    private final class SourceIcon extends JPanel {
        private final Image sourceIcon;

        {
            setOpaque(false);
            sourceIcon = ImageUtil.getScaled(model.sourceIconUrl, TitlePanel.Size, TitlePanel.Size);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = DrawUtil.init(g);
            g2.drawImage(sourceIcon, 0, 0, null, null);
        }
    }

    public static final class Model {
        private final String title;
        private final URL url;
        private final int solutionId;
        private final Date createdAt;
        private final String createdBy;
        @Nullable
        private final String sourceName;
        private final URL sourceIconUrl;
        private final MarkPanel.Model mark;

        public Model(Model rhs) {
            this(rhs.title, rhs.url, rhs.solutionId, rhs.createdAt, rhs.createdBy, rhs.sourceName, rhs.sourceIconUrl, rhs.mark);
        }

        public Model(String title, URL url, int solutionId, Date createdAt, String createdBy, @Nullable String sourceName, URL sourceIconUrl, MarkPanel.Model mark) {
            this.title = title;
            this.url = url;
            this.solutionId = solutionId;
            this.createdAt = createdAt;
            this.createdBy = createdBy;
            this.sourceName = sourceName;
            this.sourceIconUrl = sourceIconUrl;
            this.mark = mark;
        }
    }

    @Nullable
    @Override
    public Object getData(@NonNls String dataId) {
        if (MarkPanel.SolutionId.is(dataId)) return model.solutionId;
        else return null;
    }
}
