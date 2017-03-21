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

import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public final class SamebugTabbedPaneUI extends BasicTabbedPaneUI {
    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(0, 0, 0, 0);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
        tabAreaInsets = new Insets(0, 20, 20, 0);
        contentBorderInsets = new Insets(0, 0, 0, 0);
    }

    @Override
    protected void paintTab(Graphics g, int tabPlacement,
                            Rectangle[] rects, int tabIndex,
                            Rectangle iconRect, Rectangle textRect) {
        // Override because the tabComponents will be painted by default (as child components), and we only have to paint the borders
        Rectangle tabRect = rects[tabIndex];
        int selectedIndex = tabPane.getSelectedIndex();
        boolean isSelected = selectedIndex == tabIndex;

        paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height, isSelected);
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        // Override as out border is a simple line between tabs
        // This is implemented as a line at the left side for each tabs except the first one.
        if (tabIndex > 0) {
            g.setColor(ColorService.forCurrentTheme(ColorService.Separator));
            g.drawLine(x, y, x, y + h);
        }
    }

    @Override
    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
        // NOTE Override to get rid of lifting the label of the selected tab
        return 0;
    }

    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        // NOTE Override to get rid of the magical '+ 3' pixel
        // TODO I neglected the case when there is no tabComponent
        Insets tabInsets = getTabInsets(tabPlacement, tabIndex);
        int width = tabInsets.left + tabInsets.right;
        Component tabComponent = tabPane.getTabComponentAt(tabIndex);
        if (tabComponent != null) {
            width += tabComponent.getPreferredSize().width;
        }
        return width;
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
    }
}
