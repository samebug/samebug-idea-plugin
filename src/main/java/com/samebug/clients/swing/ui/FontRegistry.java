package com.samebug.clients.swing.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public final class FontRegistry {
    private static final String AvenirRegular = "AvenirNextLTPro-Regular";
    private static final String AvenirDemi = "AvenirNextLTPro-Demi";
    private static final float ExpectedAscentOfAvenirRegular = 0.848999f;

    private static boolean useCorrection = false;

    public static void registerFonts() throws IOException, FontFormatException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        final InputStream isDemi = FontRegistry.class.getResourceAsStream("/com/samebug/fonts/AvenirNextLTPro-Demi.ttf");
        final InputStream isRegular = FontRegistry.class.getResourceAsStream("/com/samebug/fonts/AvenirNextLTPro-Regular.ttf");

        final Font demi = Font.createFont(Font.TRUETYPE_FONT, isDemi);
        final Font regular = Font.createFont(Font.TRUETYPE_FONT, isRegular);

        isDemi.close();
        isRegular.close();

        ge.registerFont(demi);
        ge.registerFont(regular);

        useCorrection = doesFontNeedsCorrection(regular, ExpectedAscentOfAvenirRegular);
    }

    public static Font regular(int size) {
        Font f = new Font(AvenirRegular, Font.PLAIN, size);
        return f.deriveFont(correction(size));
    }

    public static Font demi(int size) {
        Font f = new Font(AvenirDemi, Font.PLAIN, size);
        return f.deriveFont(correction(size));
    }

    /**
     * This is an ugly workaround for an OpenJDK bug,
     * see https://bugs.openjdk.java.net/browse/JDK-8017773 or https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=671443
     * <p>
     * OpenJDK has a wrong perception on font metrics, so we try to correct that with affine transformation on the font.
     * One problem is that perfect correction is not possible with only affine transformation.
     * Another problem is that this correction is not necessary on openjdk jvms which are already patched.
     * <p>
     * NOTE this method is hardcoded for Avenir, but simple to generalize by extracting the transformation constants.
     *
     * @param scale the size of the font we use the correction for
     * @return the correction transformation, which is identity if correction is not needed
     */
    private static AffineTransform correction(float scale) {
        AffineTransform t = new AffineTransform();
        if (useCorrection) {
            t.concatenate(AffineTransform.getTranslateInstance(0.0, 0.125 * scale));
            t.concatenate(AffineTransform.getScaleInstance(1.0, 0.962));
        }
        return t;
    }


    private static boolean doesFontNeedsCorrection(Font font, float expectedAscent) {
        if (!sun.font.FontUtilities.isOpenJDK) return false;
        else {
            try {
                Canvas c = new Canvas();
                FontMetrics fm = c.getFontMetrics(font);
                Float actualAscent = getPrivateField(fm, "ascent");
                // we need correction only if the ascent is not close enough to the expected one
                return Math.abs(expectedAscent - actualAscent) > 0.01f;
            } catch (Throwable ignore) {
                // if something went wrong, it's safer not to correct
                return false;
            }
        }
    }

    private static float getPrivateField(FontMetrics fm, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field h = fm.getClass().getDeclaredField(fieldName);
        h.setAccessible(true);
        return h.getFloat(fm);
    }
}
