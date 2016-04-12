package com.samebug.clients.idea.ui.views;

import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 4/12/16.
 */
public class WriteTipHintView extends JPanel {
    public WriteTipHintView(final ActionHandler actionHandler) {

        final CTALabel ctaLabel = new CTALabel();
        final CTAExplainLabel ctaExplainLabel = new CTAExplainLabel();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JPanel() {
            {
                setLayout(new FlowLayout());
                setBorder(BorderFactory.createEmptyBorder());
                setOpaque(false);
                add(ctaLabel);
                add(ctaExplainLabel);
            }
        });

        ctaLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                actionHandler.onCTAClick();
            }
        });
    }

    @Override
    public Color getBackground() {
        return ColorUtil.writeTipPanel();
    }

    class CTALabel extends JLabel {
        {
            setText(SamebugBundle.message("samebug.tip.write.cta"));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    class CTAExplainLabel extends JLabel {
        {
            setText(SamebugBundle.message("samebug.tip.write.ctaExplain"));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    public interface ActionHandler {
        void onCTAClick();
    }
}
