package com.samebug.clients.idea.ui;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public final class FontRegistry {
    public static final String AvenirRegular = "AvenirNextLTPro-Regular";
    public static final String AvenirDemi = "AvenirNextLTPro-Demi";

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
