package com.samebug.clients.idea.ui.views.components;

import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 4/12/16.
 */
public class TipText extends JTextArea {
    public TipText(final String tipBody){
        HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.SIZE, 16);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        setFont(getFont().deriveFont(attributes));
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setBackground(null);
        setOpaque(false);
        setBorder(null);
        setText(tipBody);
    }

    @Override
    public Color getForeground() {
        return ColorUtil.emphasizedText();
    }
}
