/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.views;

import com.samebug.clients.common.entities.ExceptionType;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.components.ExceptionMessageLabel;
import com.samebug.clients.idea.ui.components.LegacyBreadcrumbBar;
import com.samebug.clients.idea.ui.components.LinkLabel;
import com.samebug.clients.idea.ui.components.SourceIcon;
import com.samebug.clients.search.api.entities.legacy.BreadCrumb;
import com.samebug.clients.search.api.entities.legacy.RestHit;
import com.samebug.clients.search.api.entities.legacy.SolutionReference;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 3/29/16.
 */
public class ExternalSolutionView extends JPanel {
    final RestHit<SolutionReference> solution;
    final java.util.List<BreadCrumb> searchBreadcrumb;
    final ExceptionType exceptionType;

    public ExternalSolutionView(RestHit<SolutionReference> solution, java.util.List<BreadCrumb> searchBreadcrumb) {
        this.solution = solution;
        this.searchBreadcrumb = searchBreadcrumb;
        exceptionType = new ExceptionType(solution.exception.typeName);

        final JPanel breadcrumbPanel = new LegacyBreadcrumbBar(searchBreadcrumb.subList(0, solution.matchLevel));
        final JPanel titlePanel = new SolutionTitlePanel();
        final JLabel exceptionMessageLabel = new ExceptionMessageLabel(solution.exception.message);
        final JPanel exceptionTypePanel = new ExceptionTypePanel();
        final JPanel sourceReferencePanel = new SourceReferencePanel(solution.solution);
        final JPanel actionPanel = new ActionPanel();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.cardSeparator));
                setOpaque(false);
                add(titlePanel, BorderLayout.NORTH);
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(new JPanel() {
                    {
                        setLayout(new BorderLayout());
                        setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
                        setOpaque(false);
                        add(exceptionTypePanel, BorderLayout.NORTH);
                        add(actionPanel, BorderLayout.SOUTH);
                        add(new JPanel() {
                            {
                                setLayout(new BorderLayout());
                                setBorder(BorderFactory.createEmptyBorder());
                                setOpaque(false);
                                add(sourceReferencePanel, BorderLayout.SOUTH);
                                add(new JPanel() {
                                    {
                                        setLayout(new BorderLayout());
                                        setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                                        setOpaque(false);
                                        add(exceptionMessageLabel, BorderLayout.CENTER);
                                    }
                                }, BorderLayout.CENTER);

                            }
                        }, BorderLayout.CENTER);
                    }
                }, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);

        setPreferredSize(new Dimension(400, getPreferredSize().height));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.min(getPreferredSize().height, 250)));
    }

    public class SolutionTitlePanel extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            final Image sourceIcon = ImageUtil.getScaled(solution.solution.source.iconUrl, 32, 32);
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

    class ExceptionTypePanel extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            add(new JLabel() {
                {
                    setText((String.format("%s", exceptionType.className)));
                    HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                    attributes.put(TextAttribute.SIZE, 14);
                    attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                    setFont(getFont().deriveFont(attributes));
                }

                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasizedText();
                }
            }, BorderLayout.CENTER);
        }
    }

    public class SourceReferencePanel extends JPanel {
        public SourceReferencePanel(@NotNull SolutionReference solutionReference) {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            if (solutionReference.author == null) {
                add(new JLabel(String.format("%s", TextUtil.prettyTime(solutionReference.createdAt))) {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.unemphasizedText();
                    }
                });
            } else {
                add(new JLabel(String.format("%s | by ", TextUtil.prettyTime(solutionReference.createdAt))) {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.unemphasizedText();
                    }
                });
                add(new LinkLabel(solutionReference.author.name, solutionReference.author.url) {
                    @Override
                    public Color getForeground() {
                        return ColorUtil.emphasizedText();
                    }
                });
            }
        }

    }

    public class ActionPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setOpaque(false);
        }
    }
}
