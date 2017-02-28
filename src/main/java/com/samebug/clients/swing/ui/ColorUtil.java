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
package com.samebug.clients.swing.ui;

import com.samebug.clients.swing.ui.component.util.interaction.Colors;

import java.awt.*;

public abstract class ColorUtil  {
    private static ColorUtil INSTANCE = null;

    public static void install(ColorUtil instance) {
        assert INSTANCE == null : "ColorUtil has already been installed";
        INSTANCE = instance;
    }

    public static <T> T forCurrentTheme(T[] objects) {
        return INSTANCE._forCurrentTheme(objects);
    }

    public final static Color[] Background = new Color[]{new Color(0xFFFFFF), new Color(0x242526)};
    public final static Color[] EmphasizedText = new Color[]{new Color(0x000000), new Color(0xCBCDCF)};
    public final static Color[] Text = new Color[]{new Color(0x333333), new Color(0x8B8C8F)};
    public final static Color[] UnemphasizedText = new Color[]{new Color(0x88BCCE), new Color(0x8AB0D6)};
    public final static Color[] Separator = new Color[]{new Color(0xE5E5E5), new Color(0x454546)};
    public final static Color[] SelectedTab = new Color[]{new Color(0x666666), new Color(0xCBCDCF)};
    public final static Color[] Tip = new Color[]{new Color(0xE0F2F8), new Color(0x0560A2)};
    public final static Color[] TipText = new Color[]{new Color(0x086a8b), new Color(0xDBEFFF)};
    public final static Color[] ScrollbarTrack = new Color[]{new Color(0xF5F5F5), new Color(0x2D2E2F)};
    public final static Color[] ScrollbarThumb = new Color[]{new Color(0xD5D5D5), new Color(0x484A4B)};

    public final static Colors[] LinkInteraction = new Colors[]{
            new Colors(new Color(0xFF8000), new Color(0xFEA144), new Color(0xED7700)),
            new Colors(new Color(0xFF8820), new Color(0xFEA144), new Color(0xED7700))
    };

    public final static Colors[] SecondaryLinkInteraction = new Colors[]{
            new Colors(new Color(0x333333), new Color(0xFEA144), new Color(0xED7700)),
            new Colors(new Color(0xCBCDCF), new Color(0xFEA144), new Color(0xED7700))
    };

    public final static Colors[] MarkInteraction = new Colors[]{
            new Colors(new Color(0x3E85DE), new Color(0x66A6F6), new Color(0x3E85DE)),
            new Colors(new Color(0x7CD2FF), new Color(0xBBE8FF), new Color(0x7CD2FF))
    };

    public final static Color[] Link = new Color[LinkInteraction.length];

    public final static Color[] SecondaryLink = new Color[SecondaryLinkInteraction.length];

    public final static Color[] Mark = new Color[MarkInteraction.length];

    protected abstract <T> T _forCurrentTheme(T[] objects);
}
