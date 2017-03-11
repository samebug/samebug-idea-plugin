package com.samebug.clients.swing.ui.component.tipRequest;

import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;

import javax.swing.*;

public class ExceptionPreview extends SamebugLabel {
    public ExceptionPreview(String preview) {
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setForegroundColor(ColorService.ExceptionPreviewText);
        setBackgroundColor(ColorService.ExceptionPreviewBackground);
        setFont(FontService.regular(14));
        setText(preview);
    }
}
