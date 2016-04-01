package com.samebug.clients.idea.ui.components;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.search.api.entities.legacy.Tip;
import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Created by poroszd on 4/1/16.
 */
public class TipSourceReferencePanel extends JPanel {
    static final PrettyTime pretty = new PrettyTime(Locale.US);

    public TipSourceReferencePanel(@NotNull Tip tip) {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(false);
        if (tip.via == null) {
            // no source, show only tip timestamp
            add(new JLabel(String.format("%s", pretty.format(tip.createdAt))) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasizedText();
                }
            });
        } else if (tip.via.author == null) {
            // source without author
            add(new JLabel(String.format("%s | via ", pretty.format(tip.createdAt))) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasizedText();
                }
            });
            add(new LinkLabel(tip.via.source.name, tip.via.url) {
                @Override
                public Color getForeground() {
                    return ColorUtil.emphasizedText();
                }
            });
        } else {
            // source with author
            add(new JLabel(String.format("%s | ", pretty.format(tip.createdAt))) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasizedText();
                }
            });
            add(new LinkLabel(tip.via.author.name, tip.via.author.url) {
                @Override
                public Color getForeground() {
                    return ColorUtil.emphasizedText();
                }
            });
            add(new JLabel(" via ") {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasizedText();
                }
            });
            add(new LinkLabel(tip.via.source.name, tip.via.url) {
                @Override
                public Color getForeground() {
                    return ColorUtil.emphasizedText();
                }
            });
        }
    }
}
