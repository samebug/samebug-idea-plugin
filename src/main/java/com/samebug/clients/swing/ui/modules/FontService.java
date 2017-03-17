/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.modules;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public final class FontService {
    private static final String Regular = "Montserrat Regular";
    private static final String Bold = "Montserrat Medium";
    private static final float ExpectedAscentOfAvenirRegular = 0.848999f;

    private static boolean useCorrection = false;

    public static void registerFonts() throws IOException, FontFormatException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        final InputStream streamBold = FontService.class.getResourceAsStream("/com/samebug/fonts/Montserrat-Medium.ttf");
        final InputStream streamRegular = FontService.class.getResourceAsStream("/com/samebug/fonts/Montserrat-Regular.ttf");

        final Font bold = Font.createFont(Font.TRUETYPE_FONT, streamBold);
        final Font regular = Font.createFont(Font.TRUETYPE_FONT, streamRegular);

        streamBold.close();
        streamRegular.close();

        ge.registerFont(bold);
        ge.registerFont(regular);
    }

    public static Font regular(int size) {
        return new Font(Regular, Font.PLAIN, size - 1);
    }

    public static Font demi(int size) {
        return new Font(Bold, Font.PLAIN, size - 1);
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
     * NOTE luckily enough, for Montserrat correction is not needed
     *
     * @param scale the size of the font we use the correction for
     * @return the correction transformation, which is identity if correction is not needed
     */
    private static AffineTransform correctionForAvenir(float scale) {
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
