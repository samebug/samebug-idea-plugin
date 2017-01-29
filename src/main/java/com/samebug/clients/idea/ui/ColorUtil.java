/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui;

import com.intellij.util.ui.UIUtil;
import com.samebug.clients.common.ui.Colors;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

final public class ColorUtil {
    @Nullable
    public static Color unemphasizedText() {
        return normalOrDarcula(Colors.unemphasizedText, Colors.unemphasizedTextDarcula);
    }

    @Nullable
    public static Color emphasizedText() {
        return normalOrDarcula(Colors.emphasizedText, Colors.emphasizedTextDarcula);
    }

    @Nullable
    public static Color alertPanel() {
        return normalOrDarcula(Colors.alertPanel, Colors.alertPanelDarcula);
    }

    @Nullable
    public static Color highlightPanel() {
        return normalOrDarcula(Colors.highlightPanel, Colors.highlightPanelDarcula);
    }

    @Nullable
    public static Color writeTipPanel() {
        return normalOrDarcula(Colors.writeTipPanel, Colors.writeTipPanelDarcula);
    }

    @Nullable
    public static Color sourceIconBackground() {
        return normalOrDarcula(Colors.sourceIconBackground, Colors.sourceIconBackgroundDarcula);
    }

    @Nullable
    public static Color ctaButton() {
        return normalOrDarcula(Colors.ctaButton, Colors.ctaButtonDarcula);
    }

    @Nullable
    public static Color button() {
        return normalOrDarcula(Colors.button, Colors.buttonDarcula);
    }

    @Nullable
    public static Color componentColors(int color) {
        return normalOrDarcula(Colors.componentColors[color], Colors.componentColorsDarcula[color]);
    }

    static Color normalOrDarcula(Color normal, Color darcula) {
        if (UIUtil.isUnderDarcula()) {
            return darcula;
        } else {
            return normal;
        }
    }
}
