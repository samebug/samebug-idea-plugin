package com.samebug.clients.swing.ui.component.popup;

import com.samebug.clients.common.ui.component.popup.ITipRequestPopup;
import com.samebug.clients.swing.ui.TestDialog;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;

@Ignore
public class TipRequestPopupTest extends TestDialog {
    @Test
    public void popup() {
        waitToInitializeUI("tipRequestPopup/1.json").showDialog();
    }

    public void initializeUI(String resourceJson) throws Exception {
        final ITipRequestPopup.Model model = readJson(resourceJson, ITipRequestPopup.Model.class);
        setPreferredSize(new Dimension(320, 180));

        TipRequestPopup f = new TipRequestPopup(model);
        setContentPane(f);
    }
}