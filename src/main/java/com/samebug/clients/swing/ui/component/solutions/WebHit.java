package com.samebug.clients.swing.ui.component.solutions;

import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.common.ui.component.solutions.IWebHit;
import com.samebug.clients.swing.ui.DrawUtil;
import com.samebug.clients.swing.ui.FontRegistry;
import com.samebug.clients.swing.ui.ImageUtil;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import com.samebug.clients.swing.ui.component.util.multiline.LinkMultilineLabel;
import com.samebug.clients.swing.ui.component.util.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;

public final class WebHit extends SamebugPanel implements IWebHit, DataProvider {
    private final Model model;
    private final MessageBus messageBus;

    private final MarkButton markButton;

    public WebHit(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        markButton = new MarkButton(messageBus, model.mark);
        final TitlePanel titlePanel = new TitlePanel();

        setLayout(new MigLayout("fillx", "0[300]0", "0[]16[]0"));

        add(titlePanel, "growx, cell 0 0");
        add(markButton, "cell 0 1");
    }

    private final class TitlePanel extends TransparentPanel {
        private final static int Size = 40;

        {
            final SourceIcon sourceIcon = new SourceIcon();
            final TitleLabel title = new TitleLabel();
            final SourceLabel source = new SourceLabel();

            setLayout(new MigLayout("", "0[]9[]0", "0[]0[]0"));
            add(sourceIcon, MessageFormat.format("w {0}!, h {0}!, cell 0 0, span 1 2, ay top", Size));
            add(title, MessageFormat.format("wmin 0, hmax {0}, growx, cell 1 0", Size));
            add(source, "wmin 0, growx, cell 1 1");

            title.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().urlClicked(model.url);
                }
            });
        }
    }

    private final class TitleLabel extends LinkMultilineLabel {
        {
            setFont(FontRegistry.demi(16));
            setText(model.title);
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

    private final class SourceLabel extends SamebugLabel {
        {
            setFont(FontRegistry.regular(12));
            String sourceText;
            if (model.createdBy == null) {
                sourceText = model.sourceName + " | " + String.format("%s", TextUtil.prettyTime(model.createdAt));
            } else {
                sourceText = model.sourceName + " by " + model.createdBy + " | " + String.format("%s", TextUtil.prettyTime(model.createdAt));
            }
            setText(sourceText);
        }
    }

    private final class SourceIcon extends TransparentPanel {
        private final Image sourceIcon;

        {
            sourceIcon = ImageUtil.getScaled(model.sourceIconUrl, TitlePanel.Size, TitlePanel.Size);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = DrawUtil.init(g);
            g2.drawImage(sourceIcon, 0, 0, null, null);
        }
    }

    @Nullable
    @Override
    public Object getData(@NonNls String dataId) {
        if (MarkButton.SolutionId.is(dataId)) return model.solutionId;
        else return null;
    }

    private Listener getListener() {
        return messageBus.syncPublisher(Listener.TOPIC);
    }

}
