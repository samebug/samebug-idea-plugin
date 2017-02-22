package com.samebug.clients.idea.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.idea.ui.font.HackFont2D;
import sun.font.*;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class FontRegistry {
    public static final String AvenirRegular = "AvenirNextLTPro-Regular";
    public static final String AvenirDemi = "AvenirNextLTPro-Demi";

    final static Logger LOGGER = Logger.getInstance(FontRegistry.class);

    public static void registerFonts() throws IOException, FontFormatException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        final InputStream isDemi = FontRegistry.class.getResourceAsStream("/com/samebug/fonts/AvenirNextLTPro-Demi.ttf");
        final InputStream isRegular = FontRegistry.class.getResourceAsStream("/com/samebug/fonts/AvenirNextLTPro-Regular.ttf");

        final Font demi = Font.createFont(Font.TRUETYPE_FONT, isDemi);
        final Font regular = Font.createFont(Font.TRUETYPE_FONT, isRegular);

        try {
            final Font2D hackDemi = new HackFont2D((TrueTypeFont) FontAccess.getFontAccess().getFont2D(demi));
            final Font2D hackRegular = new HackFont2D((TrueTypeFont) FontAccess.getFontAccess().getFont2D(regular));
            FontAccess.getFontAccess().setFont2D(demi, new Font2DHandle(hackDemi));
            FontAccess.getFontAccess().setFont2D(regular, new Font2DHandle(hackRegular));
        } catch (FontFormatException ignore) {
            // then we don't do it
            ignore.printStackTrace();
        }
        isDemi.close();
        isRegular.close();

        ge.registerFont(demi);
        ge.registerFont(regular);

        try {
            printFontMetrics(ge.getAllFonts());
        } catch (Throwable e) {
            LOGGER.warn("failed to print font metrics", e);
        }
    }

    private static void serializeFont(Font font) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream("/home/poroszd/tmp/x/demi.ser");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oos.writeObject(font);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printFontMetrics(Font[] fonts) throws Throwable {

        Arrays.sort(fonts, new Comparator<Font>() {
            @Override
            public int compare(Font font, Font t1) {
                return font.getFontName().compareTo(t1.getFontName());
            }
        });

        PrintWriter pw = new PrintWriter(new File("/home/poroszd/out.csv"));
        java.util.List<String> fieldNames = Arrays.asList("fontName", "fAscent", "ascent", "fDescent", "descent", "fLeading", "leading", "fMaxAdvance", "maxAdvance", "height");
        for (int i = 0; i < fieldNames.size() - 1; ++i) pw.print(fieldNames.get(i) + ", ");
        pw.println(fieldNames.get(fieldNames.size() - 1));

        Canvas c = new Canvas();
        for (Font f : fonts) {
            Map<String, String> fields = new HashMap<String, String>();

            FontMetrics fm = c.getFontMetrics(f);

            fields.put("fontName", f.getFontName());
            fields.put("ascent", Integer.toString(fm.getAscent()));
            fields.put("descent", Integer.toString(fm.getDescent()));
            fields.put("leading", Integer.toString(fm.getLeading()));
            fields.put("maxAdvance", Integer.toString(fm.getMaxAdvance()));
            fields.put("height", Integer.toString(fm.getHeight()));
            Float ascent = getPrivateField(fm, "ascent");
            if (ascent != null) fields.put("fAscent", ascent.toString());
            Float descent = getPrivateField(fm, "descent");
            if (descent != null) fields.put("fDescent", descent.toString());
            Float leading = getPrivateField(fm, "leading");
            if (leading != null) fields.put("fLeading", leading.toString());
            Float maxAdvance = getPrivateField(fm, "maxAdvance");
            if (maxAdvance != null) fields.put("fMaxAdvance", maxAdvance.toString());

            for (int i = 0; i < fieldNames.size() - 1; ++i) pw.print(fields.get(fieldNames.get(i)) + ", ");
            pw.println(fields.get(fieldNames.get(fieldNames.size() - 1)));
        }

        pw.close();
    }

    private static Float getPrivateField(FontMetrics fm, String fieldName) {
        try {
            Field h = fm.getClass().getDeclaredField(fieldName);
            h.setAccessible(true);
            return h.getFloat(fm);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
