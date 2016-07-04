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

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.ui.Colors;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.messages.view.SearchGroupCardListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.component.ExceptionMessageLabel;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.entities.TextSearchGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;

final public class TextSearchGroupCard extends SearchGroupCard {
    public final TextSearchGroup searchGroup;
    public final JLabel hitsLabel;
    public final JLabel titleLabel;
    public final ExceptionMessageLabel queryLabel;

    public TextSearchGroupCard(final TextSearchGroup searchGroup) {
        super(searchGroup);
        this.searchGroup = searchGroup;

        hitsLabel = new TextSearchGroupCard.HitsLabel();
        titleLabel = new TextSearchGroupCard.QueryLabel();
        queryLabel = new ExceptionMessageLabel(searchGroup.lastSearch.query);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.cardSeparator));
        add(new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
                add(new TransparentPanel() {
                    {
                        add(hitsLabel, BorderLayout.EAST);
                    }
                }, BorderLayout.NORTH);
                add(new TransparentPanel() {
                    {
                        add(groupInfoPanel, BorderLayout.SOUTH);
                        add(new TransparentPanel() {
                            {
                                setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
                                add(titleLabel, BorderLayout.NORTH);
                                add(queryLabel, BorderLayout.CENTER);
                            }
                        }, BorderLayout.CENTER);
                    }
                }, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);

        setPreferredSize(new Dimension(400, Math.min(getPreferredSize().height, 250)));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.min(getPreferredSize().height, 250)));
    }

    @Override
    SearchGroup getSearchGroup() {
        return searchGroup;
    }

    final class QueryLabel extends JLabel {
        {
            setText(SamebugBundle.message("samebug.history.search.text.title"));
            setForeground(Colors.samebugOrange);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            attributes.put(TextAttribute.SIZE, 16);
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            setFont(getFont().deriveFont(attributes));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    TabController tab = ToolWindowController.DATA_KEY.getData(DataManager.getInstance().getDataContext(TextSearchGroupCard.this));
                    Project project = DataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(TextSearchGroupCard.this));
                    if (tab != null && project != null) project.getMessageBus().syncPublisher(SearchGroupCardListener.TOPIC).titleClick(tab, searchGroup);
                }
            });
        }
    }

    final class HitsLabel extends JLabel {
        static final int LIMIT = 100;

        {
            if (searchGroup.numberOfHits > LIMIT) {
                setText(String.format("%d+ hits", LIMIT));
            } else {
                setText(String.format("%d hits", searchGroup.numberOfHits));
            }
        }

        @Override
        public Color getForeground() {
            return ColorUtil.unemphasizedText();
        }
    }
}
