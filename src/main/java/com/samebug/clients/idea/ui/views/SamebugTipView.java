package com.samebug.clients.idea.ui.views;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.components.AvatarIcon;
import com.samebug.clients.idea.ui.components.LegacyBreadcrumbBar;
import com.samebug.clients.idea.ui.components.LinkLabel;
import com.samebug.clients.idea.ui.components.TipSourceReferencePanel;
import com.samebug.clients.search.api.entities.legacy.BreadCrumb;
import com.samebug.clients.search.api.entities.legacy.RestHit;
import com.samebug.clients.search.api.entities.legacy.Tip;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 3/29/16.
 */
public class SamebugTipView {
    final RestHit<Tip> tip;
    final java.util.List<BreadCrumb> searchBreadcrumb;

    public JPanel controlPanel;
    public ProfilePanel profilePanel;
    public TipLabel tipLabel;
    public TipSourceReferencePanel sourceReferencePanel;
    public JPanel actionPanel;
    public JPanel breadcrumbPanel;
    public TipContentPanel tipContentPanel;

    public SamebugTipView(RestHit<Tip> tip, java.util.List<BreadCrumb> searchBreadcrumb) {
        this.tip = tip;
        this.searchBreadcrumb = searchBreadcrumb;

        controlPanel = new ControlPanel();
        breadcrumbPanel = new LegacyBreadcrumbBar(searchBreadcrumb.subList(0, tip.matchLevel));
        tipLabel = new TipLabel();
        sourceReferencePanel = new TipSourceReferencePanel(tip.solution);
        profilePanel = new ProfilePanel();
        actionPanel = new ActionPanel();
        tipContentPanel = new TipContentPanel();

        controlPanel.add(new JPanel() {
            {
                setLayout(new BorderLayout(0, 0));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
                setOpaque(false);
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout(0, 0));
                        setBorder(BorderFactory.createEmptyBorder());
                        setOpaque(false);
                        add(actionPanel, BorderLayout.SOUTH);
                        add(tipContentPanel, BorderLayout.CENTER);
                    }
                }, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);
    }



    public class ControlPanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        }
        @Override
        public Color getBackground() {
            return ColorUtil.highlightPanel();
        }
    }

    public class TipLabel extends JTextArea {
        {
            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.SIZE, 16);
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            setFont(getFont().deriveFont(attributes));
            setEditable(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setBackground(null);
            setOpaque(false);
            setBorder(null);
            setText(tip.solution.tip);
            setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 150)));
            setMaximumSize(new Dimension(getMaximumSize().width, 150));
        }
        @Override
        public Color getForeground() {
            return ColorUtil.emphasizedText();
        }
    }

    public class ProfilePanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            setOpaque(false);
            setPreferredSize(new Dimension(74, 100));
            final Image profile = ImageUtil.getScaled(tip.solution.author.avatarUrl, 64, 64);
            add(new AvatarIcon(profile), BorderLayout.NORTH);
            add(new JPanel() {
                {
                    setLayout(new BorderLayout(0, 0));
                    setBorder(BorderFactory.createEmptyBorder());
                    setOpaque(false);
                    add(new LinkLabel(tip.solution.author.name, tip.solution.author.url) {
                        {
                            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                            attributes.put(TextAttribute.SIZE, 10);
                            setFont(getFont().deriveFont(attributes));
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setHorizontalTextPosition(SwingConstants.CENTER);
                        }
                    }, BorderLayout.NORTH);
                    add(new JPanel() {
                        {
                            setLayout(new BorderLayout(0, 0));
                            setBorder(BorderFactory.createEmptyBorder());
                            setOpaque(false);
                        }
                    }, BorderLayout.CENTER);
                }
            }, BorderLayout.CENTER);
        }
    }

    public class ActionPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setOpaque(false);
        }
    }

    public class TipContentPanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            setOpaque(false);
            add(profilePanel, BorderLayout.WEST);
            add(new JPanel() {
                {
                    setLayout(new BorderLayout(0, 0));
                    setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                    setOpaque(false);
                    add(tipLabel, BorderLayout.NORTH);
                    add(new JPanel(){
                        {
                            setLayout(new BorderLayout(0, 0));
                            setBorder(BorderFactory.createEmptyBorder());
                            setOpaque(false);
                            add(sourceReferencePanel, BorderLayout.NORTH);
                            add(new JPanel(){
                                {
                                    setOpaque(false);
                                }
                            }, BorderLayout.CENTER);
                        }
                    }, BorderLayout.CENTER);
                }
            }, BorderLayout.CENTER);
        }
    }
}
