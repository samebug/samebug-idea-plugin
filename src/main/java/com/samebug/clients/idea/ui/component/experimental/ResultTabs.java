package com.samebug.clients.idea.ui.component.experimental;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class ResultTabs extends JTabbedPane {
    public ResultTabs() {
        WebResultsTab webResultsTab = new WebResultsTab();
        TipResultsTab tipResultsTab = new TipResultsTab();
        CrashDocTab crashDocTab = new CrashDocTab();

        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        setBackground(Color.white);
        setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 16));
        setTabLayoutPolicy(SCROLL_TAB_LAYOUT);

        setUI(new SamebugTabbedPaneUI());

        addTab("Samebug tips", null, tipResultsTab);
        addTab("Solutions on the net", null, webResultsTab);
        addTab("CrashDoc", null, crashDocTab);

        setTabComponentAt(0, new JPanel() {
            {

                setOpaque(false);

                add(new JLabel("Samebug tips") {
                    {
                        setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 16));
                    }
                });
                add(new JLabel("999") {
                    final Font font = new Font(Constants.AvenirDemi, Font.PLAIN, 10);
                    {
                        setBorder(BorderFactory.createEmptyBorder(2,6,2,6));
                    }
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D)g;
                        g2.setRenderingHint(
                                RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                        );
                        g2.setRenderingHint(
                                RenderingHints.KEY_COLOR_RENDERING,
                                RenderingHints.VALUE_COLOR_RENDER_QUALITY
                        );
                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );
                        g.setColor(Constants.SamebugOrange);
                        g.fillRoundRect(0, 0, getWidth() -1, getHeight() - 1, 18, 18);
                        g2.setFont(font);
                        g2.setColor(Color.white);
                        g2.drawString("999", 8, 12);
                    }
                });

            }
        });
    }
}

final class SamebugTabbedPaneUI extends BasicTabbedPaneUI {
    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(7, 20, 7, 20);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
        tabAreaInsets = new Insets(0, 0, 1, 0);
        contentBorderInsets = new Insets(0, 0, 0, 0);
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // Intentionally do nothing
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        g.setColor(lightHighlight);

        switch (tabPlacement) {
            case LEFT:
                // TODO
                break;
            case RIGHT:
                // TODO
                break;
            case BOTTOM:
                // TODO
                break;
            case TOP:
            default:
                if (isSelected) {
                    g.setColor(Constants.SeparatorColor);
                    g.drawLine(x, y + h - 1, x, y); // left highlight
                    g.drawLine(x, y, x + w, y); // top highlight
                    g.drawLine(x + w, y, x + w, y + h - 1); // right highlight
                }
        }
    }

    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {

        int tabCount = tabPane.getTabCount();

        Rectangle iconRect = new Rectangle(),
                textRect = new Rectangle();
        Rectangle clipRect = g.getClipBounds();

        g.setColor(Constants.SeparatorColor);
        Rectangle firstTab = tabPane.getBoundsAt(0);
        Rectangle selectedTab = tabPane.getBoundsAt(selectedIndex);

        int base = clipRect.y + clipRect.height - 1;
        g.drawLine(clipRect.x, base, clipRect.x + selectedTab.x - firstTab.x, base);
        g.drawLine(clipRect.x + selectedTab.x + selectedTab.width - firstTab.x, base, clipRect.x + clipRect.width, base);

        // Paint tabRuns of tabs from back to front
        for (int i = runCount - 1; i >= 0; i--) {
            int start = tabRuns[i];
            int next = tabRuns[(i == runCount - 1)? 0 : i + 1];
            int end = (next != 0? next - 1: tabCount - 1);
            for (int j = start; j <= end; j++) {
                if (j != selectedIndex && rects[j].intersects(clipRect)) {
                    paintTab(g, tabPlacement, rects, j, iconRect, textRect);
                }
            }
        }

        // Paint selected tab if its in the front run
        // since it may overlap other tabs
        if (selectedIndex >= 0 && rects[selectedIndex].intersects(clipRect)) {
            paintTab(g, tabPlacement, rects, selectedIndex, iconRect, textRect);
        }

    }

    @Override
    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
        return 0;
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        g.setColor(tabPane.getBackground());
        g.fillRect(x,y,w,h);
    }
}
