/**
 * Copyright 2016 Samebug, Inc.
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

import java.awt.*;

/**
 * Created by poroszd on 3/31/16.
 */
public class ColorUtil {
    public static Color unemphasizedText() {
        return normalOrDarcula(Colors.unemphasizedText, Colors.unemphasizedTextDarcula);
    }

    public static Color emphasizedText() {
        return normalOrDarcula(Colors.emphasizedText, Colors.emphasizedTextDarcula);
    }

    public static Color alertPanel() {
        return normalOrDarcula(Colors.alertPanel, Colors.alertPanelDarcula);
    }

    public static Color highlightPanel() {
        return normalOrDarcula(Colors.highlightPanel, Colors.highlightPanelDarcula);
    }

    public static Color writeTipPanel() {
        return normalOrDarcula(Colors.writeTipPanel, Colors.writeTipPanelDarcula);
    }

    public static Color sourceIconBackground() {
        return normalOrDarcula(Colors.sourceIconBackground, Colors.sourceIconBackgroundDarcula);
    }

    public static Color ctaButton() {
        return normalOrDarcula(Colors.ctaButton, Colors.ctaButtonDarcula);
    }

    public static Color button() {
        return normalOrDarcula(Colors.button, Colors.buttonDarcula);
    }

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
