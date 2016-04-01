package com.samebug.clients.idea.ui.views;

import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.components.*;
import com.samebug.clients.search.api.entities.legacy.BreadCrumb;
import com.samebug.clients.search.api.entities.legacy.RestHit;
import com.samebug.clients.search.api.entities.legacy.SolutionReference;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 3/29/16.
 */
public class ExternalSolutionView {
    final RestHit<SolutionReference> solution;
    final java.util.List<BreadCrumb> searchBreadcrumb;
    final String packageName;
    final String className;

    public JPanel controlPanel;
    public JPanel titlePanel;
    public ExceptionPanel exceptionPanel;
    public SourceReferencePanel sourceReferencePanel;
    public JPanel actionPanel;
    public JPanel breadcrumbPanel;

    public ExternalSolutionView(RestHit<SolutionReference> solution, java.util.List<BreadCrumb> searchBreadcrumb) {
        this.solution = solution;
        this.searchBreadcrumb = searchBreadcrumb;

        int dotIndex = solution.exception.typeName.lastIndexOf('.');
        if (dotIndex < 0) {
            this.packageName = null;
            this.className = solution.exception.typeName;
        } else {
            this.packageName = solution.exception.typeName.substring(0, dotIndex);
            this.className = solution.exception.typeName.substring(dotIndex + 1);
        }

        controlPanel = new ControlPanel();
        breadcrumbPanel = new LegacyBreadcrumbBar(searchBreadcrumb.subList(0, solution.matchLevel));
        titlePanel = new TitlePanel();
        exceptionPanel = new ExceptionPanel();
        sourceReferencePanel = new SourceReferencePanel(solution.solution);
        actionPanel = new ActionPanel();

        controlPanel.add(new JPanel() {
            {
                setLayout(new BorderLayout(0, 0));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
                setOpaque(false);
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(titlePanel, BorderLayout.NORTH);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout(0, 0));
                        setBorder(BorderFactory.createEmptyBorder());
                        setOpaque(false);
                        add(actionPanel, BorderLayout.SOUTH);
                        add(new JPanel() {
                            {
                                setLayout(new BorderLayout(0, 0));
                                setBorder(BorderFactory.createEmptyBorder());
                                setOpaque(false);
                                add(sourceReferencePanel, BorderLayout.SOUTH);
                                add(new JPanel(){
                                    {
                                        setLayout(new BorderLayout());
                                        setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                                        setOpaque(false);
                                        add(exceptionPanel, BorderLayout.CENTER);
                                    }
                                });

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

    public class TitlePanel extends JPanel {
        {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            // TODO load image asynchronously
            final Image sourceIcon = ImageUtil.getImage(solution.solution.source.iconUrl);
            add(new SourceIcon(sourceIcon), BorderLayout.WEST);
            add(new JPanel() {
                {
                    setLayout(new BorderLayout());
                    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                    setOpaque(false);
                    add(new LinkLabel(solution.solution.title, solution.solution.url) {
                        {
                            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                            attributes.put(TextAttribute.SIZE, 16);
                            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                            setFont(getFont().deriveFont(attributes));
                            setForeground(Colors.samebugOrange);
                        }
                    }, BorderLayout.CENTER);
                }
            });
        }
    }

    public class ExceptionPanel extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            add(new JLabel(String.format("%s", className)), BorderLayout.NORTH);
            add(new ExceptionMessageLabel(solution.exception.message), BorderLayout.CENTER);
        }
    }



    public class ActionPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setOpaque(false);
        }
    }
}
