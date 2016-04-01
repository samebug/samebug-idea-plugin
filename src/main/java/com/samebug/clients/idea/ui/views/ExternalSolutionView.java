package com.samebug.clients.idea.ui.views;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.components.BreadcrumbBar;
import com.samebug.clients.idea.ui.components.LinkLabel;
import com.samebug.clients.search.api.entities.ExternalSolution;
import org.apache.commons.lang.StringEscapeUtils;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by poroszd on 3/29/16.
 */
public class ExternalSolutionView {
    static final PrettyTime pretty = new PrettyTime(Locale.US);

    final ExternalSolution solution;
    final String packageName;
    final String className;

    public JPanel controlPanel;
    public JPanel titlePanel;
    public JLabel messageLabel;
    public JLabel titleLabel;
    public SourceReferencePanel sourceReferencePanel;
    public JPanel actionPanel;
    public JPanel breadcrumbPanel;

    public ExternalSolutionView(ExternalSolution solution) {
        this.solution = solution;
        int dotIndex = solution.exception.typeName.lastIndexOf('.');
        if (dotIndex < 0) {
            this.packageName = null;
            this.className = solution.exception.typeName;
        } else {
            this.packageName = solution.exception.typeName.substring(0, dotIndex);
            this.className = solution.exception.typeName.substring(dotIndex + 1);
        }

        controlPanel = new ControlPanel();
        breadcrumbPanel = new BreadcrumbBar(solution.componentStack);
        titlePanel = new TitlePanel();
        titleLabel = new TitleLabel();
        messageLabel = new MessageLabel();
        sourceReferencePanel = new SourceReferencePanel();
        actionPanel = new ActionPanel();

        controlPanel.add(new JPanel() {
            {
                setLayout(new BorderLayout(0, 0));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(titlePanel, BorderLayout.NORTH);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout(0, 0));
                        setBorder(BorderFactory.createEmptyBorder());
                        add(actionPanel, BorderLayout.SOUTH);
                        add(new JPanel() {
                            {
                                setLayout(new BorderLayout(0, 0));
                                setBorder(BorderFactory.createEmptyBorder());
                                add(sourceReferencePanel, BorderLayout.SOUTH);
                                add(titleLabel, BorderLayout.NORTH);
                                add(messageLabel, BorderLayout.CENTER);

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
            setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        }
    }

    public class SourceReferencePanel extends JPanel {
        {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            setBorder(BorderFactory.createEmptyBorder());
            add(new JLabel(String.format("%s | by ", pretty.format(solution.document.createdAt))) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasized();
                }
            });
            add(new LinkLabel(solution.document.author.name, solution.document.author.url) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasized();
                }
            });
        }

    }

    public class TitlePanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder());
            // TODO load image asynchronously
            // TODO do not use getScaledInstance; https://community.oracle.com/docs/DOC-983611
            final Image sourceIcon = ImageUtil.getImage(solution.document.source.iconUrl).getScaledInstance(32, 32, Image.SCALE_FAST);
            add(new JPanel() {
                {
                    setBorder(BorderFactory.createEmptyBorder());
                    add(new JLabel(new ImageIcon(sourceIcon)));
                }
            }, BorderLayout.WEST);
            add(new LinkLabel(solution.title, solution.document.url) {
                {
                    HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                    attributes.put(TextAttribute.SIZE, 16);
                    setFont(getFont().deriveFont(attributes));
                    setForeground(Colors.samebugOrange);
                }
            }, BorderLayout.CENTER);
        }
    }

    public class MessageLabel extends JLabel {
        private final String escapedText;

        public MessageLabel() {
            String message = solution.exception.message;
            if (message == null) {
                escapedText = String.format("<html><i>No message provided</i></html>");
            } else {
                // Escape html, but keep line breaks
                String broken = StringEscapeUtils.escapeHtml(message).replaceAll("\\n", "<br>");
                escapedText = String.format("<html>%s</html>", broken);
            }
        }

        @Override
        public String getText() {
            return escapedText;
        }

        @Override
        public int getVerticalAlignment() {
            return SwingConstants.TOP;
        }
    }

    public class TitleLabel extends JLabel {
        @Override
        public String getText() {
            return String.format("%s", className);
        }

        @Override
        public Color getForeground() {
            return Colors.samebugOrange;
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
}
