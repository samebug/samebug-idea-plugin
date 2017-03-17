package com.samebug.clients.swing.ui.font;

import com.samebug.clients.swing.ui.modules.FontService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;

public class FontSizeTest {
    @Test
    public void printFontMetrics() throws NoSuchFieldException, IllegalAccessException {
        Font font = FontService.regular(14);
        Canvas c = new Canvas();
        FontMetrics fm = c.getFontMetrics(font);
        System.out.println("Are we under OpenJDK? " + sun.font.FontUtilities.isOpenJDK);
        System.out.println("Ascent:" + getPrivateField(fm, "ascent"));
        System.out.println("Descent: " + getPrivateField(fm, "descent"));
        System.out.println("Leading: " + getPrivateField(fm, "leading"));
        System.out.println("Max advance: " + getPrivateField(fm, "maxAdvance"));
        System.out.println("Height: " + getPrivateField(fm, "height"));
    }

    @BeforeClass
    public static void registerFonts() throws IOException, FontFormatException {
        FontService.registerFonts();
    }

    private static float getPrivateField(FontMetrics fm, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field h = fm.getClass().getDeclaredField(fieldName);
        h.setAccessible(true);
        return h.getFloat(fm);
    }
}
