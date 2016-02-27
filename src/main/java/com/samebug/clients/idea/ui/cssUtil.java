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

import com.samebug.clients.search.api.SamebugClient;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * Created by poroszd on 2/24/16.
 */
public class cssUtil {
    static String getUiThemeId() {
        String themeName = UIManager.getLookAndFeel().getName();
        String themeId;
        if (themeName.equals("IntelliJ")) {
            themeId = "intellij";
        } else if (themeName.equals("Darcula")) {
            themeId = "darcula";
        } else {
            themeId = "intellij";
        }
        return themeId;
    }

    static void updatePaneStyleSheet(JEditorPane pane) {
        HTMLEditorKit kit = (HTMLEditorKit) pane.getEditorKit();
        StyleSheet ss = kit.getStyleSheet();
        String themeId = cssUtil.getUiThemeId();
        ss.importStyleSheet(SamebugClient.getHistoryCssUrl(themeId));
        kit.setStyleSheet(ss);
    }
}
