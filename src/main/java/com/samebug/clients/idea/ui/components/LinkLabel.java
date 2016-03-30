package com.samebug.clients.idea.ui.components;

import com.intellij.ide.BrowserUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by poroszd on 3/29/16.
 */
public class LinkLabel extends JLabel {
    final private String text;
    final private URL link;

    public LinkLabel(final String text, final URL link) {
        super(text);
        this.text = text;
        this.link = link;
        if (link != null) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BrowserUtil.browse(link);
                }
            });
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            setFont(getFont().deriveFont(attributes));
        }
    }
}
