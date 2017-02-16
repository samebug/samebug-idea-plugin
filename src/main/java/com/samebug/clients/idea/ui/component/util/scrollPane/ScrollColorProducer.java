package com.samebug.clients.idea.ui.component.util.scrollPane;

import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.NotNullProducer;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.ui.components.JBScrollPane.BRIGHTNESS_FROM_VIEW;

final class ScrollColorProducer implements NotNullProducer<Color> {
    private final Component myComponent;
    private final Color myBrightColor;
    private final Color myDarkColor;
    private final boolean isBackground;
    // cached color values
    private volatile Color myOriginal;
    private volatile Color myModified;

    @SuppressWarnings("UseJBColor")
    private ScrollColorProducer(Component component, int bright, int dark, boolean background) {
        myComponent = component;
        myBrightColor = new Color(bright);
        myDarkColor = new Color(dark);
        isBackground = background;
    }

    @NotNull
    @Override
    public Color produce() {
        Container parent = myComponent.getParent();
        if (isBackground && parent instanceof JScrollPane && Registry.is("ide.scroll.background.auto")) {
            Color background = Color.blue; //JBScrollPane.getViewBackground((JScrollPane)parent);
            if (background != null) {
                if (!background.equals(myOriginal)) {
                    myModified = ColorUtil.shift(background, ColorUtil.isDark(background) ? 1.05 : 0.96);
                    myOriginal = background;
                }
                return myModified;
            }
        }
        return isDark(myComponent) ? myDarkColor : myBrightColor;
    }

    static void setForeground(Component component) {
        component.setForeground(new JBColor(new ScrollColorProducer(component, 0xE6E6E6, 0x3C3F41, false)));
    }

    static void setBackground(Component component) {
        component.setBackground(new JBColor(new ScrollColorProducer(component, 0xF5F5F5, 0x3C3F41, true)));
    }

    static boolean isDark(Component c) {
        Container parent = c.getParent();
        if (parent instanceof JScrollPane) {
            JScrollPane pane = (JScrollPane)parent;
            Object property = pane.getClientProperty(BRIGHTNESS_FROM_VIEW);
            if (property instanceof Boolean && (Boolean)property) {
                Color color = Color.blue; //JBScrollPane.getViewBackground(pane);
                if (color != null) return ColorUtil.isDark(color);
            }
        }
        return UIUtil.isUnderDarcula();
    }
}
