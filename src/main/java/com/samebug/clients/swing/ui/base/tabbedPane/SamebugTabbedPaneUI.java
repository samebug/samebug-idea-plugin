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

public class SamebugTabbedPaneUI extends BasicTabbedPaneUI {
    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(10, 0, 10, 0);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
        tabAreaInsets = new Insets(0, 20, 10, 0);
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
        // Override as our border is a simple line between tabs
        // This is implemented as a line at the left side for each tabs except the first one.
        g.setColor(ColorService.forCurrentTheme(ColorService.Separator));
        if (isSelected) {
            g.drawLine(x, y + h, x, y);
            g.drawLine(x, y, x + w, y);
            g.drawLine(x + w, y, x + w, y + h);
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
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        Rectangle r = new Rectangle();
        getTabBounds(selectedIndex, r);
        g.setColor(ColorService.forCurrentTheme(ColorService.Separator));
        int startX = x + 20;
        int endX = x + w;
        if (startX < r.x) g.drawLine(startX, y - 10, r.x, y - 10);
        if (r.x + r.width < endX) g.drawLine(r.x + r.width, y - 10, endX, y - 10);
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}

    // This is overridden to make sure that the tabs will not scroll
    @Override
    protected LayoutManager createLayoutManager() {
        return new TabbedPaneLayout() {
            // This method is almost exact copy of the super, but uses a fictional large size instead of the tabPane
            @Override
            protected void calculateTabRects(int tabPlacement, int tabCount) {
                FontMetrics metrics = getFontMetrics();
                Insets insets = tabPane.getInsets();
                Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
                int selectedIndex = tabPane.getSelectedIndex();
                int tabRunOverlay;
                int i, j;
                int x, y;

                //
                // Calculate bounds within which a tab run must fit
                //
                switch (tabPlacement) {
                    case LEFT:
                    case RIGHT:
                    case BOTTOM:
                        throw new UnsupportedOperationException();
                    case TOP:
                    default:
                        maxTabHeight = calculateMaxTabHeight(tabPlacement);
                        x = insets.left + tabAreaInsets.left;
                        y = insets.top + tabAreaInsets.top;
                        break;
                }

                tabRunOverlay = getTabRunOverlay(tabPlacement);

                runCount = 0;
                selectedRun = -1;

                if (tabCount == 0) {
                    return;
                }

                // Run through tabs and partition them into runs
                Rectangle rect;
                for (i = 0; i < tabCount; i++) {
                    rect = rects[i];

                    // Tabs on TOP or BOTTOM....
                    if (i > 0) {
                        rect.x = rects[i - 1].x + rects[i - 1].width;
                    } else {
                        tabRuns[0] = 0;
                        runCount = 1;
                        maxTabWidth = 0;
                        rect.x = x;
                    }
                    rect.width = calculateTabWidth(tabPlacement, i, metrics);
                    maxTabWidth = Math.max(maxTabWidth, rect.width);

                    // Initialize y position in case there's just one run
                    rect.y = y;
                    rect.height = maxTabHeight/* - 2*/;
                    if (i == selectedIndex) {
                        selectedRun = runCount - 1;
                    }
                }

                // Step through runs from back to front to calculate
                // tab y locations and to pad runs appropriately
                for (i = runCount - 1; i >= 0; i--) {
                    int start = tabRuns[i];
                    int next = tabRuns[i == (runCount - 1) ? 0 : i + 1];
                    int end = (next != 0 ? next - 1 : tabCount - 1);
                    for (j = start; j <= end; j++) {
                        rect = rects[j];
                        rect.y = y;
                        rect.x += getTabRunIndent(tabPlacement, i);
                    }
                    y += (maxTabHeight - tabRunOverlay);
                }
            }
        };
    }
}
