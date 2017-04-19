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
package com.samebug.clients.swing.ui.modules;

import com.samebug.clients.swing.ui.base.form.FormColors;
import com.samebug.clients.swing.ui.base.interaction.InteractionColors;

import java.awt.*;

public abstract class ColorService {
    /**
     * Poor man's dependency injection for singletons.
     * <p>
     * In the swing implementation of the UI there are some things that we cannot implement barely in swing.
     * E.g. how do we load the images, how do we decide which color theme to use, etc.
     * I do not want to hardcode IntelliJ code here, because that would make it impossible to run separate tests
     * for the UI (which makes the implementation of new component much easier).
     * <p>
     * These parts are implemented in the following structure:
     * - the whole public interface of the class is actually static, and should be used as a singleton
     * - we have an abstract class, that has a private static instance member
     * - the class has the necessary interface as protected abstract methods
     * - the class has the necessary interface also as public static methods, that calls the respective method of the private instance
     * - the class has a public static 'install' method to set the private instance field.
     * - install can be called only once
     * <p>
     * It has all the drawbacks of using singleton instances, but it makes the UI components code more clean an easier to use and test.
     */
    private static ColorService INSTANCE = null;

    public static void install(ColorService instance) {
        assert INSTANCE == null : "ColorService has already been installed";
        INSTANCE = instance;
    }

    public static <T> T forCurrentTheme(T[] objects) {
        return INSTANCE.internalForCurrentTheme(objects);
    }

    public static <T> T forLightTheme(T[] objects) {
        if (objects == null) return null;
        else return objects[0];
    }

    public static <T> T forDarkTheme(T[] objects) {
        if (objects == null) return null;
        else if (objects.length > 1) return objects[1];
        else return objects[0];
    }

    public final static Color[] Background = new Color[]{new Color(0xFFFFFF), new Color(0x242526)};
    public final static Color[] EmphasizedText = new Color[]{new Color(0x000000), new Color(0xCBCDCF)};
    public final static Color[] Text = new Color[]{new Color(0x333333), new Color(0x8B8C8F)};
    public final static Color[] UnemphasizedText = new Color[]{new Color(0x88BCCE), new Color(0x8AB0D6)};
    public final static Color[] Separator = new Color[]{new Color(0xE5E5E5), new Color(0x454546)};
    public final static Color[] SelectedTab = new Color[]{new Color(0x666666), new Color(0xCBCDCF)};
    public final static Color[] Tip = new Color[]{new Color(0xE0F2F8), new Color(0x0560A2)};
    public final static Color[] TipText = new Color[]{new Color(0x086a8b), new Color(0xDBEFFF)};
    public final static Color[] ExceptionPreviewText = new Color[]{new Color(0x2985A4), new Color(0x80BEEF)};
    public final static Color[] ExceptionPreviewBackground = new Color[]{new Color(0xC1E7F3), new Color(0x094778)};
    public final static Color[] ScrollbarTrack = new Color[]{new Color(0xF5F5F5), new Color(0x2D2E2F)};
    public final static Color[] ScrollbarThumb = new Color[]{new Color(0xD5D5D5), new Color(0x484A4B)};
    public final static Color[] ErrorBar = new Color[]{new Color(0xF8F4C8), new Color(0xF2EBAB)};
    public final static Color[] LoadingArc = new Color[]{new Color(0x3379A1), new Color(0x61B6E7)};

    public static final FormColors NormalForm = new FormColors(
            new Color[]{new Color(0xB2B2B2), new Color(0x666768)},
            new Color[]{new Color(0x333333), new Color(0xC8C8C9)},
            new Color[]{new Color(0xE80D0D), new Color(0xFF1B1B)},
            new Color[]{new Color(0xFFFFFF), new Color(0x242526)},
            new Color[]{new Color(0x000000), new Color(0xCBCDCF)},
            new Color[]{new Color(0xE80D0D), new Color(0xFF1B1B)},
            new Color[]{new Color(0x333333), new Color(0x8B8C8E)},
            new Color[]{new Color(0xAAAAAA), new Color(0x666768)}
    );

    public static final FormColors TipForm = new FormColors(
            new Color[]{new Color(0xB5D7E2), new Color(0x2072AE)},
            new Color[]{new Color(0x3379A1), new Color(0x61B6E7)},
            new Color[]{new Color(0xE80D0D), new Color(0xFF5644)},
            new Color[]{new Color(0xFFFFFF), new Color(0x083960)},
            new Color[]{new Color(0x000000), new Color(0xDBEFFF)},
            new Color[]{new Color(0xE80D0D), new Color(0xFF5644)},
            new Color[]{new Color(0x086a8b), new Color(0xDBEFFF)},
            new Color[]{new Color(0xAAAAAA), new Color(0x3682BE)}
    );

    public final static InteractionColors LinkInteraction = new InteractionColors(
            new Color[]{new Color(0xFF8000), new Color(0xFF8000)},
            new Color[]{new Color(0xFEA144), new Color(0xFEA144)},
            new Color[]{new Color(0xED7700), new Color(0xED7700)},
            new Color[]{new Color(0xFEA144), new Color(0xFEA144)}
    );

    public final static InteractionColors SecondaryLinkInteraction = new InteractionColors(
            new Color[]{new Color(0x333333), new Color(0xCBCDCF)},
            new Color[]{new Color(0xFEA144), new Color(0xFEA144)},
            new Color[]{new Color(0xED7700), new Color(0xED7700)},
            new Color[]{new Color(0xFEA144), new Color(0xFEA144)}
    );

    public final static InteractionColors MarkInteraction = new InteractionColors(
            new Color[]{new Color(0x3E85DE), new Color(0x7CD2FF)},
            new Color[]{new Color(0x66A6F6), new Color(0xBBE8FF)},
            new Color[]{new Color(0x3E85DE), new Color(0x7CD2FF)},
            new Color[]{new Color(0x81B6C8), new Color(0x78A6D4)}
    );

    public final static InteractionColors GoogleInteraction = new InteractionColors(
            new Color[]{new Color(0x3E85DE), new Color(0x7CD2FF)},
            new Color[]{new Color(0x66A6F6), new Color(0xBBE8FF)},
            new Color[]{new Color(0x3E85DE), new Color(0x7CD2FF)},
            new Color[]{new Color(0x81B6C8), new Color(0x78A6D4)}
    );

    public final static InteractionColors FacebookInteraction = new InteractionColors(
            new Color[]{new Color(0x3E85DE), new Color(0x7CD2FF)},
            new Color[]{new Color(0x66A6F6), new Color(0xBBE8FF)},
            new Color[]{new Color(0x3E85DE), new Color(0x7CD2FF)},
            new Color[]{new Color(0x81B6C8), new Color(0x78A6D4)}
    );

    protected abstract <T> T internalForCurrentTheme(T[] objects);
}
