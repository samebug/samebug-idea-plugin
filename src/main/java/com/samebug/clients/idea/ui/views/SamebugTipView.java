package com.samebug.clients.idea.ui.views;

import com.samebug.clients.idea.ui.components.BreadcrumbBar;
import com.samebug.clients.idea.ui.components.LinkLabel;
import com.samebug.clients.search.api.entities.SamebugTip;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Created by poroszd on 3/29/16.
 */
public class SamebugTipView {
    static final PrettyTime pretty = new PrettyTime(Locale.US);

    final SamebugTip tip;

    public ControlPanel controlPanel;
    public JPanel breadcrumbPanel;
    public TipLabel tipLabel;
    public SourceReferencePanel sourceReferencePanel;
    public ProfilePanel profilePanel;

    public SamebugTipView(SamebugTip tip) {
        this.tip = tip;

        controlPanel = new ControlPanel();
        breadcrumbPanel = new BreadcrumbBar(tip.componentStack);
        tipLabel = new TipLabel();
        sourceReferencePanel = new SourceReferencePanel();
        profilePanel = new ProfilePanel();

        controlPanel.add(new JPanel() {
            {
                setLayout(new BorderLayout(0, 0));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout(0, 0));
                        setBorder(BorderFactory.createEmptyBorder());
                        add(sourceReferencePanel, BorderLayout.SOUTH);
                        add(new JPanel() {
                            {
                                setLayout(new BorderLayout(0, 0));
                                setBorder(BorderFactory.createEmptyBorder());
                                add(profilePanel, BorderLayout.WEST);
                                add(tipLabel, BorderLayout.CENTER);
                            }
                        }, BorderLayout.CENTER);
                    }
                }, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);
    }



    public class ControlPanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }
        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(400, d.height);
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(Integer.MAX_VALUE, Integer.min(d.height, 250));
        }
    }

    public class TipLabel extends JLabel {
        @Override
        public String getText() {
            return tip.tipText;
        }
    }

    public class SourceReferencePanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setBorder(BorderFactory.createEmptyBorder());
            add(new JLabel(String.format("%s | ", pretty.format(tip.updated))));
            add(new LinkLabel(tip.sourceAuthorName, tip.sourceAuthorUrl));
            add(new JLabel(" via "));
            add(new LinkLabel(tip.sourceName, tip.sourceUrl));
        }
    }

    public class ProfilePanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setBorder(BorderFactory.createEmptyBorder());
            add(new JLabel((Icon)null));
            add(new LinkLabel(tip.samebugAuthorName, tip.samebugAuthorUrl));
        }
    }
}
