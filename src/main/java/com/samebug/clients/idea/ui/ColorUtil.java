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
    public static Color highlightPanel() {
        return normalOrDarcula(Colors.highlightPanel, Colors.highlightPanelDarcula);
    }


    static Color normalOrDarcula(Color normal, Color darcula) {
        if (UIUtil.isUnderDarcula()) {
            return darcula;
        } else {
            return normal;
        }
    }
}
