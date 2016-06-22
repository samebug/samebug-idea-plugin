package com.samebug.clients.idea.ui.component.organism;

import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.search.api.entities.SearchGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

final public class GroupInfoPanel extends TransparentPanel {

    public GroupInfoPanel(@NotNull final SearchGroup searchGroup){
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        add(new JLabel() {
            {
                String text;
                if (searchGroup.numberOfSearches == 1) {
                    text = String.format("%s", TextUtil.prettyTime(searchGroup.lastSeen));
                } else {
                    text = String.format("%s | %d times, first %s",
                            TextUtil.prettyTime(searchGroup.lastSeen), searchGroup.numberOfSearches, TextUtil.prettyTime(searchGroup.firstSeen));
                }

                setText(text);
            }

            @Override
            public Color getForeground() {
                return ColorUtil.unemphasizedText();
            }
        });
    }
}
