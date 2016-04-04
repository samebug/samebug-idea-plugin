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
