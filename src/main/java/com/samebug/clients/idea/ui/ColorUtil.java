package com.samebug.clients.idea.ui;

import com.intellij.util.ui.UIUtil;

import java.awt.*;

/**
 * Created by poroszd on 3/31/16.
 */
public class ColorUtil {
    public static Color unemphasized() {
        if (UIUtil.isUnderDarcula()) {
            return Colors.unemphasizedDarcula;
        } else {
            return Colors.unemphasized;
        }

    }
}
