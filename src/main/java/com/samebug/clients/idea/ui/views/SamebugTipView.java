package com.samebug.clients.idea.ui.views;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.components.BreadcrumbBar;
import com.samebug.clients.idea.ui.components.LinkLabel;
import com.samebug.clients.search.api.entities.Tip;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by poroszd on 3/29/16.
 */
public class SamebugTipView {
    static final PrettyTime pretty = new PrettyTime(Locale.US);

    final Tip tip;

    public JPanel controlPanel;
    public ProfilePanel profilePanel;
    public TipLabel tipLabel;
    public SourceReferencePanel sourceReferencePanel;
    public JPanel actionPanel;
    public JPanel breadcrumbPanel;
    public TipContentPanel tipContentPanel;

    public SamebugTipView(Tip tip) {
        this.tip = tip;

        controlPanel = new ControlPanel();
        breadcrumbPanel = new BreadcrumbBar(tip.componentStack);
        tipLabel = new TipLabel();
        sourceReferencePanel = new SourceReferencePanel();
        profilePanel = new ProfilePanel();
        actionPanel = new ActionPanel();
        tipContentPanel = new TipContentPanel();

        controlPanel.add(new JPanel() {
            {
                setLayout(new BorderLayout(0, 0));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout(0, 0));
                        setBorder(BorderFactory.createEmptyBorder());
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
            setBorder(null);
            setText(tip.tipText);
            setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 150)));
            setMaximumSize(new Dimension(getMaximumSize().width, 150));
        }
    }

    public class SourceReferencePanel extends JPanel {
        {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            setBorder(BorderFactory.createEmptyBorder());
            add(new JLabel(String.format("%s | ", pretty.format(tip.document.createdAt))) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasized();
                }
            });
            add(new LinkLabel(tip.referencedSolution.document.author.name, tip.referencedSolution.document.author.url) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasized();
                }
            });
            add(new JLabel(" via ") {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasized();
                }
            });
            add(new LinkLabel(tip.referencedSolution.document.source.name, tip.referencedSolution.document.url) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasized();
                }
            });
        }

    }

    public class ProfilePanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            setPreferredSize(new Dimension(74, 100));
            // TODO load image asynchronously
            // TODO do not use getScaledInstance; https://community.oracle.com/docs/DOC-983611
            final Image profile = ImageUtil.getImage(tip.document.author.avatarUrl).getScaledInstance(64, 64, Image.SCALE_FAST);
            add(new JPanel() {
                {
                    setBorder(BorderFactory.createEmptyBorder());
                    add(new JLabel(new ImageIcon(profile)));
                }
            }, BorderLayout.NORTH);
            add(new JPanel() {
                {
                    setLayout(new BorderLayout(0, 0));
                    setBorder(BorderFactory.createEmptyBorder());
                    add(new LinkLabel(tip.document.author.name, tip.document.author.url) {
                        {
                            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                            attributes.put(TextAttribute.SIZE, 10);
                            setFont(getFont().deriveFont(attributes));
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setHorizontalTextPosition(SwingConstants.CENTER);
                        }
                    }, BorderLayout.NORTH);
                    add(new JPanel(), BorderLayout.CENTER);
                }
            }, BorderLayout.CENTER);
        }
    }

    public class ActionPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            add(new JLabel("helped 42 people") {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasized();
                }
            });
        }
    }

    public class TipContentPanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            add(profilePanel, BorderLayout.WEST);
            add(new JPanel() {
                {
                    setLayout(new BorderLayout(0, 0));
                    setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                    add(tipLabel, BorderLayout.NORTH);
                    add(new JPanel(){
                        {
                            setLayout(new BorderLayout(0, 0));
                            setBorder(BorderFactory.createEmptyBorder());
                            add(sourceReferencePanel, BorderLayout.NORTH);
                            add(new JPanel(), BorderLayout.CENTER);
                        }
                    }, BorderLayout.CENTER);
                }
            }, BorderLayout.CENTER);
        }
    }
}
