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
        // Override as our border is a simple line between tabs
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

    // This is overridden to make sure that the tabs will not scroll
    @Override
    protected LayoutManager createLayoutManager() {
        return new TabbedPaneLayout() {
            // This method is almost exact copy of the super, but uses a fictional large size instead of the tabPane
            @Override
            protected void calculateTabRects(int tabPlacement, int tabCount) {
                FontMetrics metrics = getFontMetrics();
                // This method is almost exact copy of the super, but uses a fictional large size instead of the tabPane's real size
//                Dimension size = tabPane.getSize();
                Dimension size = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
                Insets insets = tabPane.getInsets();
                Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
                int fontHeight = metrics.getHeight();
                int selectedIndex = tabPane.getSelectedIndex();
                int tabRunOverlay;
                int i, j;
                int x, y;
                int returnAt;
                boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
                boolean leftToRight = true;

                //
                // Calculate bounds within which a tab run must fit
                //
                switch (tabPlacement) {
                    case LEFT:
                        maxTabWidth = calculateMaxTabWidth(tabPlacement);
                        x = insets.left + tabAreaInsets.left;
                        y = insets.top + tabAreaInsets.top;
                        returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
                        break;
                    case RIGHT:
                        maxTabWidth = calculateMaxTabWidth(tabPlacement);
                        x = size.width - insets.right - tabAreaInsets.right - maxTabWidth;
                        y = insets.top + tabAreaInsets.top;
                        returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
                        break;
                    case BOTTOM:
                        maxTabHeight = calculateMaxTabHeight(tabPlacement);
                        x = insets.left + tabAreaInsets.left;
                        y = size.height - insets.bottom - tabAreaInsets.bottom - maxTabHeight;
                        returnAt = size.width - (insets.right + tabAreaInsets.right);
                        break;
                    case TOP:
                    default:
                        maxTabHeight = calculateMaxTabHeight(tabPlacement);
                        x = insets.left + tabAreaInsets.left;
                        y = insets.top + tabAreaInsets.top;
                        returnAt = size.width - (insets.right + tabAreaInsets.right);
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

                    if (!verticalTabRuns) {
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

                        // Never move a TAB down a run if it is in the first column.
                        // Even if there isn't enough room, moving it to a fresh
                        // line won't help.
                        if (rect.x != x && rect.x + rect.width > returnAt) {
                            if (runCount > tabRuns.length - 1) {
                                expandTabRunsArray();
                            }
                            tabRuns[runCount] = i;
                            runCount++;
                            rect.x = x;
                        }
                        // Initialize y position in case there's just one run
                        rect.y = y;
                        rect.height = maxTabHeight/* - 2*/;

                    } else {
                        // Tabs on LEFT or RIGHT...
                        if (i > 0) {
                            rect.y = rects[i - 1].y + rects[i - 1].height;
                        } else {
                            tabRuns[0] = 0;
                            runCount = 1;
                            maxTabHeight = 0;
                            rect.y = y;
                        }
                        rect.height = calculateTabHeight(tabPlacement, i, fontHeight);
                        maxTabHeight = Math.max(maxTabHeight, rect.height);

                        // Never move a TAB over a run if it is in the first run.
                        // Even if there isn't enough room, moving it to a fresh
                        // column won't help.
                        if (rect.y != y && rect.y + rect.height > returnAt) {
                            if (runCount > tabRuns.length - 1) {
                                expandTabRunsArray();
                            }
                            tabRuns[runCount] = i;
                            runCount++;
                            rect.y = y;
                        }
                        // Initialize x position in case there's just one column
                        rect.x = x;
                        rect.width = maxTabWidth/* - 2*/;

                    }
                    if (i == selectedIndex) {
                        selectedRun = runCount - 1;
                    }
                }

                if (runCount > 1) {
                    // Re-distribute tabs in case last run has leftover space
                    normalizeTabRuns(tabPlacement, tabCount, verticalTabRuns ? y : x, returnAt);

                    selectedRun = getRunForTab(tabCount, selectedIndex);

                    // Rotate run array so that selected run is first
                    if (shouldRotateTabRuns(tabPlacement)) {
                        rotateTabRuns(tabPlacement, selectedRun);
                    }
                }

                // Step through runs from back to front to calculate
                // tab y locations and to pad runs appropriately
                for (i = runCount - 1; i >= 0; i--) {
                    int start = tabRuns[i];
                    int next = tabRuns[i == (runCount - 1) ? 0 : i + 1];
                    int end = (next != 0 ? next - 1 : tabCount - 1);
                    if (!verticalTabRuns) {
                        for (j = start; j <= end; j++) {
                            rect = rects[j];
                            rect.y = y;
                            rect.x += getTabRunIndent(tabPlacement, i);
                        }
                        if (shouldPadTabRun(tabPlacement, i)) {
                            padTabRun(tabPlacement, start, end, returnAt);
                        }
                        if (tabPlacement == BOTTOM) {
                            y -= (maxTabHeight - tabRunOverlay);
                        } else {
                            y += (maxTabHeight - tabRunOverlay);
                        }
                    } else {
                        for (j = start; j <= end; j++) {
                            rect = rects[j];
                            rect.x = x;
                            rect.y += getTabRunIndent(tabPlacement, i);
                        }
                        if (shouldPadTabRun(tabPlacement, i)) {
                            padTabRun(tabPlacement, start, end, returnAt);
                        }
                        if (tabPlacement == RIGHT) {
                            x -= (maxTabWidth - tabRunOverlay);
                        } else {
                            x += (maxTabWidth - tabRunOverlay);
                        }
                    }
                }

                // Pad the selected tab so that it appears raised in front
                padSelectedTab(tabPlacement, selectedIndex);

                // if right to left and tab placement on the top or
                // the bottom, flip x positions and adjust by widths
                if (!leftToRight && !verticalTabRuns) {
                    int rightMargin = size.width
                            - (insets.right + tabAreaInsets.right);
                    for (i = 0; i < tabCount; i++) {
                        rects[i].x = rightMargin - rects[i].x - rects[i].width;
                    }
                }
            }
        };
    }
}
