/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui;

import com.intellij.util.ui.UIUtil;

import java.awt.*;

final public class ColorUtil {
    public static Color background() {
        return colorForCurrentTheme(Background);
    }

    public static Color samebug() {
        return colorForCurrentTheme(SamebugOrange);
    }

    public static Color emphasizedText() {
        return colorForCurrentTheme(EmphasizedText);
    }

    public static Color text() {
        return colorForCurrentTheme(Text);
    }

    public static Color unemphasizedText() {
        return colorForCurrentTheme(UnemphasizedText);
    }

    public static Color selectedTab() {
        return colorForCurrentTheme(SelectedTab);
    }

    public static Color separator() {
        return colorForCurrentTheme(Separator);
    }

    public static Color mark() {
        return colorForCurrentTheme(MarkPanel);
    }

    public static Color markedSeparator() {
        return colorForCurrentTheme(MarkedPanelSeparator);
    }

    public static Color tip() {
        return colorForCurrentTheme(Tip);
    }

    public static Color tipText() {
        return colorForCurrentTheme(TipText);
    }

    public static Color scrollbarBackground() {
        return colorForCurrentTheme(ScrollbarBackground);
    }

    public static Color scrollbarBubble() {
        return colorForCurrentTheme(ScrollbarBubble);
    }



    public static Color alertPanel() {
        return null;
    }

    public static Color highlightPanel() {
        return null;
    }

    public static Color writeTipPanel() {
        return null;
    }

    public static Color sourceIconBackground() {
        return null;
    }

    public static Color ctaButton() {
        return null;
    }

    public static Color button() {
        return null;
    }

    public static Color componentColors(int color) {
        return null;
    }


    private static Color colorForCurrentTheme(Color[] colors) {
        if (UIUtil.isUnderDarcula() && colors.length > 1) {
            return colors[1];
        } else {
            return colors[0];
        }
    }

    private final static Color[] Background = new Color[]{new Color(0xFFFFFF), new Color(0x242526)};
    private final static Color[] EmphasizedText = new Color[]{new Color(0x000000), new Color(0xCBCDCF)};
    private final static Color[] Text = new Color[]{new Color(0x333333), new Color(0x8B8C8F)};
    private final static Color[] UnemphasizedText = new Color[]{new Color(0x88BCCE), new Color(0x8AB0D6)};
    private final static Color[] Separator = new Color[]{new Color(0xE5E5E5), new Color(0x454546)};
    private final static Color[] SamebugOrange = new Color[]{new Color(0xFF8010), new Color(0xFF8820)};
    private final static Color[] SelectedTab = new Color[]{new Color(0x666666), new Color(0xCBCDCF)};
    private final static Color[] MarkPanel = new Color[]{new Color(0x4287DB), new Color(0x7CD2FF)};
    private final static Color[] MarkedPanelSeparator = new Color[]{new Color(0x3373C3), new Color(0x8CE2FF) /*TODO*/};
    private final static Color[] Tip = new Color[]{new Color(0xE0F2F8), new Color(0x0560A2)};
    private final static Color[] TipText = new Color[]{new Color(0x086a8b), new Color(0xDBEFFF)};
    private final static Color[] ScrollbarBackground = new Color[]{new Color(0xF5F5F5), new Color(0x2D2E2F)};
    private final static Color[] ScrollbarBubble = new Color[]{new Color(0xD5D5D5), new Color(0x484A4B)};
}
