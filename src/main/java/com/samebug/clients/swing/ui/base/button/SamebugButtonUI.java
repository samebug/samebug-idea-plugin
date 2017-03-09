/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.base.button;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class SamebugButtonUI extends BasicButtonUI {
    // Override because the color of the text depends on the fill style of the samebug button
    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        assert c instanceof SamebugButton : "SamebugButtonUI is only applicable to SamebugButton";
        SamebugButton b = (SamebugButton) c;
        FontMetrics fm = b.getFontMetrics(b.getFont());

        if (b.isFilled()) g.setColor(b.getBackground());
        else g.setColor(b.getForeground());

        sun.swing.SwingUtilities2.drawString(c, g, text, textRect.x + getTextShiftOffset(), textRect.y + fm.getAscent() + getTextShiftOffset());
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
    }
}
