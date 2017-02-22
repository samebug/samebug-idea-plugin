package com.samebug.clients.idea.ui.font;

import sun.font.FontStrike;
import sun.font.PhysicalFont;
import sun.font.TrueTypeFont;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.lang.reflect.Field;

public class HackFont2D extends TrueTypeFont {
    private final TrueTypeFont original;

    public HackFont2D(TrueTypeFont original) throws FontFormatException {
        super(extract(original), null, 0, true);
        this.original = original;
    }

    private static String extract(TrueTypeFont o) {
        try {
            Field f = PhysicalFont.class.getDeclaredField("platName");
            f.setAccessible(true);
            return (String) f.get(o);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public FontStrike getStrike(Font font) {
        FontStrike fs = original.getStrike(font);
        FontStrike.class.
        return null;
    }

    @Override
    public FontStrike getStrike(Font font, FontRenderContext frc) {
        return new MyFontStrike(original.getStrike(font, frc));
    }
}

