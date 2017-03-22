/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.base.tabbedPane;

import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SamebugTabbedPane extends JTabbedPane {
    private final List<SamebugTabHeader> tabs;

    // TODO updating tabs, when inserting one before the first we have to change it, etc
    // TODO cover every input interface to make sure only SamebugTabHeader can be inserted as a tab component
    {
        tabs = new ArrayList<SamebugTabHeader>();
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setFont(FontService.regular(16));
        setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
        setOpaque(false);

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                for (int i = 0; i < getTabCount(); ++i) {
                    SamebugTabHeader header = getTabComponentAt(i);
                    if (header != null) header.setSelected(false);
                }
                SamebugTabHeader selectedHeader = getTabComponentAt(getSelectedIndex());
                if (selectedHeader != null) selectedHeader.setSelected(true);
            }
        });
    }

    // TODO this addTab behaviour differs from the base class, it also adds (and returns) the tab component
    public SamebugTabHeader addLabeledTab(String tabName, @Nullable Integer hits, Component tabComponent) {
        SamebugTabHeader tabHeader;
        if (hits != null) tabHeader = new LabelAndHitsTabHeader(tabName, hits);
        else tabHeader = new LabelTabHeader(tabName);

        if (getTabCount() == 0) tabHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        else tabHeader.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        int newTabIndex = getTabCount();
        super.addTab(null, tabComponent);
        setTabComponentAt(newTabIndex, tabHeader);

        return tabHeader;
    }

    @Override
    public SamebugTabHeader getTabComponentAt(int index) {
        if (index >= tabs.size()) return null;
        else return tabs.get(index);
    }

    @Override
    public void setTabComponentAt(int index, Component component) {
        if (component != null && indexOfComponent(component) != -1) {
            throw new IllegalArgumentException("Component is already added to this JTabbedPane");
        }
        Component oldValue = getTabComponentAt(index);
        if (component != oldValue) {
            int tabComponentIndex = indexOfTabComponent(component);
            if (tabComponentIndex != -1) {
                setTabComponentAt(tabComponentIndex, null);
            }
            tabs.add(index, (SamebugTabHeader) component);
            firePropertyChange("indexForTabComponent", -1, index);
        }
    }

    @Override
    public void updateUI() {
        setBackground(ColorService.forCurrentTheme(ColorService.Background));
        setUI(new SamebugTabbedPaneUI());
    }
}
