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

    public static Color mark() {
        return colorForCurrentTheme(MarkPanelBackground);
    }

    public static Color markSeparator() {
        return colorForCurrentTheme(MarkPanelSeparator);
    }

    public static Color markedSeparator() {
        return colorForCurrentTheme(MarkedPanelSeparator);
    }

    public static Color text() {
        return colorForCurrentTheme(Text);
    }

    public static Color emphasizedText() {
        return colorForCurrentTheme(EmphasizedText);
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

    public static Color tip() {
        return colorForCurrentTheme(Tip);
    }

    public static Color tipText() {
        return colorForCurrentTheme(TipText);
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

    private final static Color[] Background = new Color[]{Color.white};
    private final static Color[] MarkPanelBackground = new Color[]{new Color(0x3E85DE)};
    private final static Color[] MarkPanelSeparator = new Color[]{new Color(0x3D85DD)};
    private final static Color[] MarkedPanelSeparator = new Color[]{new Color(0x3373C3)};
    private final static Color[] Text = new Color[]{new Color(0x333333)};
    private final static Color[] UnemphasizedText = new Color[]{new Color(0xC3C3C3)};
    private final static Color[] EmphasizedText = new Color[]{Color.black};
    private final static Color[] Separator = new Color[]{new Color(0xE5E5E5)};
    private final static Color[] SamebugOrange = new Color[]{new Color(0xFF8000)};
    private final static Color[] SelectedTab = new Color[]{new Color(0x666666)};
    private final static Color[] Tip = new Color[]{new Color(0xDFF2F8)};
    private final static Color[] TipText = new Color[]{new Color(0x006A8D)};
}
