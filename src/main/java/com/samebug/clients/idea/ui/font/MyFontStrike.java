package com.samebug.clients.idea.ui.font;

import sun.font.FontStrike;
import sun.font.PhysicalStrike;
import sun.font.StrikeMetrics;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MyFontStrike extends PhysicalStrike {
    private final FontStrike s;

    public MyFontStrike(FontStrike s) {
        this.s = s;
    }

    @Override
    public int getNumGlyphs() {
        return s.getNumGlyphs();
    }

    public long getGlyphImagePtr(int var1) {
        return 0;
    }

    @Override
    public StrikeMetrics getFontMetrics() {
        try {
            Method m = FontStrike.class.getDeclaredMethod("getFontMetrics");
            StrikeMetrics fm = (StrikeMetrics) m.invoke(s);
            fm.ascentY = 0.848999f;
            fm.descentY = 0.14282227f;
            return fm;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
