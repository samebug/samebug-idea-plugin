package com.samebug.clients.idea.components.application;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class FontRegistry {
    public static void registerFonts() throws IOException, FontFormatException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        final InputStream isDemi = FontRegistry.class.getResourceAsStream("/com/samebug/fonts/AvenirNextLTPro-Demi.ttf");
        final InputStream isRegular = FontRegistry.class.getResourceAsStream("/com/samebug/fonts/AvenirNextLTPro-Regular.ttf");

        final Font demi = Font.createFont(Font.TRUETYPE_FONT, isDemi);
        final Font regular = Font.createFont(Font.TRUETYPE_FONT, isRegular);

        ge.registerFont(demi);
        ge.registerFont(regular);
    }
}
