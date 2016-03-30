package com.samebug.clients.idea.ui.views;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 3/29/16.
 */
public class SearchTabView {
    public JPanel controlPanel;
    public JPanel contentPanel;

    public SearchTabView() {
        controlPanel = new JPanel() {
            {
                setLayout(new BorderLayout(0, 0));
                add(new JLabel("hello world"));
                add(new JScrollPane() {
                    {
                        contentPanel = new JPanel() {
                            {
                                setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                            }
                        };
                        setViewportView(contentPanel);
                        getVerticalScrollBar().setUnitIncrement(50);
                    }
                }, BorderLayout.CENTER);
            }
        };
    }
}
