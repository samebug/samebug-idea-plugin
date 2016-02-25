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
