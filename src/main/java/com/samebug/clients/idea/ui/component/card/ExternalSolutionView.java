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
package com.samebug.clients.idea.ui.component.card;

import com.samebug.clients.common.entities.ExceptionType;
import com.samebug.clients.common.ui.Colors;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.component.ExceptionMessageLabel;
import com.samebug.clients.idea.ui.component.LinkLabel;
import com.samebug.clients.idea.ui.component.SourceIcon;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.component.organism.BreadcrumbBar;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.search.api.entities.BreadCrumb;
import com.samebug.clients.search.api.entities.RestHit;
import com.samebug.clients.search.api.entities.SolutionReference;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

final public class ExternalSolutionView extends HitView {
    final Model model;
    final RestHit<SolutionReference> solution;
    final ExceptionType exceptionType;

    public final BreadcrumbBar breadcrumbPanel;
    public final JPanel titlePanel;
    public final ExceptionMessageLabel exceptionMessageLabel;
    public final JPanel exceptionTypePanel;
    public final JPanel sourceReferencePanel;

    public ExternalSolutionView(@NotNull Model model) {
        super(model);
        this.model = model;
        final java.util.List<BreadCrumb> searchBreadcrumb = model.getMatchingBreadCrumb();
//        if (searchGroup instanceof StackTraceSearchGroup) {
//            this.searchBreadcrumb = ((StackTraceSearchGroup) searchGroup).lastSearch.stackTrace.breadCrumbs;
//        } else {
//            this.searchBreadcrumb = new ArrayList<BreadCrumb>(0);
//        }
        solution = model.getHit();
        // RestHit<SolutionReference> should always have an exception
        assert solution.exception != null;

        exceptionType = new ExceptionType(solution.exception.typeName);
        breadcrumbPanel = new BreadcrumbBar(searchBreadcrumb.subList(0, solution.matchLevel));
        titlePanel = new SolutionTitlePanel();
        exceptionMessageLabel = new ExceptionMessageLabel(solution.exception.message);
        exceptionTypePanel = new ExceptionTypePanel();
        sourceReferencePanel = new SourceReferencePanel(solution.solution);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.cardSeparator));
        add(new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
                add(titlePanel, BorderLayout.NORTH);
                add(breadcrumbPanel, BorderLayout.SOUTH);
                add(new TransparentPanel() {
                    {
                        setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
                        add(exceptionTypePanel, BorderLayout.NORTH);
                        add(new TransparentPanel() {
                            {
                                setLayout(new GridBagLayout());
                                GridBagConstraints gbc = new GridBagConstraints();
                                add(markPanel, gbc);
                                gbc.gridx = 2;
                                gbc.weightx = 1;
                                add(new TransparentPanel(), gbc);
                            }
                        }, BorderLayout.SOUTH);
                        add(new TransparentPanel() {
                            {
                                add(sourceReferencePanel, BorderLayout.SOUTH);
                                add(new TransparentPanel() {
                                    {
                                        setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                                        add(exceptionMessageLabel, BorderLayout.CENTER);
                                    }
                                }, BorderLayout.CENTER);

                            }
                        }, BorderLayout.CENTER);
                    }
                }, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);

        setPreferredSize(new Dimension(400, Math.min(getPreferredSize().height, 250)));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.min(getPreferredSize().height, 250)));
    }

    final class SolutionTitlePanel extends TransparentPanel {
        {
            final Image sourceIcon = ImageUtil.getScaled(IdeaSamebugPlugin.getInstance().getUrlBuilder().sourceIcon(solution.solution.source.icon), 32, 32);
            add(new SourceIcon(sourceIcon), BorderLayout.WEST);
            add(new TransparentPanel() {
                {
                    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                    add(new LinkLabel(solution.solution.title, solution.solution.url) {
                        {
                            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                            attributes.put(TextAttribute.SIZE, 16);
                            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                            setFont(getFont().deriveFont(attributes));
                            setForeground(Colors.samebugOrange);
                            setToolTipText(SamebugBundle.message("samebug.solution.title.tooltip", solution.solution.url));
                        }
                    }, BorderLayout.CENTER);
                }
            });
        }
    }

    final class ExceptionTypePanel extends TransparentPanel {
        {
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

    final class SourceReferencePanel extends TransparentPanel {
        public SourceReferencePanel(@NotNull SolutionReference solutionReference) {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
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

    public interface Model extends MarkPanel.Model {
        @Override
        @NotNull
        RestHit<SolutionReference> getHit();

        @NotNull
        java.util.List<BreadCrumb> getMatchingBreadCrumb();
    }

}
