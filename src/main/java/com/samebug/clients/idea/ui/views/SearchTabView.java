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

        header = new HeaderPanel();
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

    class HeaderPanel extends JPanel {
        {
            setLayout(new BorderLayout());
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, Math.min(super.getPreferredSize().height, 167));
        }
    }

    class SolutionsPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }
    }
}
