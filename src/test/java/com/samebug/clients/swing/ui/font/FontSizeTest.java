package com.samebug.clients.swing.ui.font;

import com.samebug.clients.swing.ui.modules.FontService;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;

@Ignore
public class FontSizeTest {
    @Test
    public void printFontMetrics() throws NoSuchFieldException, IllegalAccessException {
        Font font = FontService.regular(14);
        Canvas c = new Canvas();
        FontMetrics fm = c.getFontMetrics(font);
        printJREInfo();
        System.out.println("Ascent:" + getPrivateField(fm, "ascent"));
        System.out.println("Descent: " + getPrivateField(fm, "descent"));
        System.out.println("Leading: " + getPrivateField(fm, "leading"));
        System.out.println("Max advance: " + getPrivateField(fm, "maxAdvance"));
        System.out.println("Height: " + getPrivateField(fm, "height"));
    }

    @Test
    public void checkTextWidth() {
        Font font = FontService.regular(14);
        Canvas c = new Canvas();
        FontMetrics fm = c.getFontMetrics(font);
        printJREInfo();
        System.out.println("width of 'mmmmmm':" + fm.stringWidth("mmmmmm"));
        System.out.println("width of 'iiiiii':" + fm.stringWidth("iiiiii"));
    }

    @BeforeClass
    public static void registerFonts() throws IOException, FontFormatException {
        FontService.registerFonts();
    }

    public static void printJREInfo() {
        System.out.println("OS name: " + System.getProperty("os.name"));
        System.out.println("OS version: " + System.getProperty("os.version"));
        System.out.println("OS arch: " + System.getProperty("os.arch"));
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Java runtime version: " + System.getProperty("java.runtime.version"));
        System.out.println("Are we under OpenJDK? " + sun.font.FontUtilities.isOpenJDK);
    }

    private static float getPrivateField(FontMetrics fm, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field h = fm.getClass().getDeclaredField(fieldName);
        h.setAccessible(true);
        return h.getFloat(fm);
    }
}
