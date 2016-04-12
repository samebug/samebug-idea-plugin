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

import com.samebug.clients.idea.ui.views.components.tip.WriteTipHint;
import com.samebug.clients.idea.ui.views.components.tip.WriteTip;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 3/29/16.
 */
public class SearchTabView {
    public JPanel controlPanel;
    public JPanel header;
    public JScrollPane scrollPane;
    public JPanel solutionsPanel;

    public SearchTabView() {

        header = new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder());
            }
        };
        scrollPane = new JScrollPane();
        solutionsPanel = new SolutionsPanel();
        controlPanel = new JPanel() {
            {
                setLayout(new BorderLayout());
                add(header, BorderLayout.NORTH);
                add(scrollPane, BorderLayout.CENTER);
            }
        };

        scrollPane.setViewportView(solutionsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    public void makeHeader(final SearchGroupCardView search) {
        header.removeAll();
        header.add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder());
                add(search, BorderLayout.CENTER);
                setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 167)));
            }
        });
    }

    public void makeHeader(final SearchGroupCardView search, final WriteTipHint writeTipHint) {
        header.removeAll();
        header.add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder());
                add(search, BorderLayout.CENTER);
                add(writeTipHint, BorderLayout.SOUTH);
                setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 167 + 50)));
            }
        });
    }

    public void makeHeader(final SearchGroupCardView search, final WriteTip writeTip) {
        header.removeAll();
        header.add(new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder());
                add(search, BorderLayout.CENTER);
                add(writeTip, BorderLayout.SOUTH);
                setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 167 + 350)));
            }
        });
    }

    class SolutionsPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }
    }
}
