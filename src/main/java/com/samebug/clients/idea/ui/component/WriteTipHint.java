/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.ui.component;

import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

public class WriteTipHint extends WriteTipCTA {
    final CTAContextLabel ctaPrefix;
    final CTAContextLabel ctaPostfix;

    public WriteTipHint() {
        ctaPrefix = new CTAContextLabel();
        ctaButton = new CTALabel();
        ctaPostfix = new CTAContextLabel();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JPanel() {
            {
                setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                setBorder(BorderFactory.createEmptyBorder());
                setOpaque(false);
                ctaPrefix.setText(SamebugBundle.message("samebug.tip.cta.small.prefix"));
                ctaPostfix.setText(SamebugBundle.message("samebug.tip.cta.small.postfix"));
                add(ctaPrefix);
                add(ctaButton);
                add(ctaPostfix);
            }
        });
        setPreferredSize(new Dimension(400, getPreferredSize().height));
    }

    @Override
    public Color getBackground() {
        return ColorUtil.writeTipPanel();
    }

    class CTALabel extends JLabel {
        {
            setText(SamebugBundle.message("samebug.tip.cta.small.link"));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            attributes.put(TextAttribute.SIZE, 16);
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            setFont(getFont().deriveFont(attributes));
        }

        @Override
        public Color getForeground() {
            return ColorUtil.emphasizedText();
        }
    }

    class CTAContextLabel extends JLabel {
        {
            final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.SIZE, 16);
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            setFont(getFont().deriveFont(attributes));
        }

        @Override
        public Color getForeground() {
            return ColorUtil.unemphasizedText();
        }
    }
}
