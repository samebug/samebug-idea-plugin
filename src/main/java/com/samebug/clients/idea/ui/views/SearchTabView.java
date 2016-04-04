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
    public JPanel solutionsPanel;

    public SearchTabView() {

        controlPanel = new JPanel() {
            {
                setLayout(new BorderLayout(0, 0));
                header = new JPanel() {
                    {
                        setLayout(new BorderLayout());
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(super.getPreferredSize().width, Math.min(super.getPreferredSize().height, 162));
                    }
                };
                add(header, BorderLayout.NORTH);
                add(new JScrollPane() {
                    {
                        solutionsPanel = new JPanel() {
                            {
                                setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                            }
                        };
                        setViewportView(solutionsPanel);
                        getVerticalScrollBar().setUnitIncrement(10);
                    }
                }, BorderLayout.CENTER);
            }
        };
    }
}
