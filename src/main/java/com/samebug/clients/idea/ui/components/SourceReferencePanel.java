package com.samebug.clients.idea.ui.components;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.search.api.entities.legacy.SolutionReference;
import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Created by poroszd on 4/1/16.
 */
public class SourceReferencePanel extends JPanel {
    static final PrettyTime pretty = new PrettyTime(Locale.US);

    public SourceReferencePanel(@NotNull SolutionReference solutionReference){
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(false);
        if (solutionReference.author == null) {
            add(new JLabel(String.format("%s", pretty.format(solutionReference.createdAt))) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasizedText();
                }
            });
        } else {
            add(new JLabel(String.format("%s | by ", pretty.format(solutionReference.createdAt))) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasizedText();
                }
            });
            add(new LinkLabel(solutionReference.author.name, solutionReference.author.url) {
                @Override
                public Color getForeground() {
                    return ColorUtil.unemphasizedText();
                }
            });
        }
    }

}
