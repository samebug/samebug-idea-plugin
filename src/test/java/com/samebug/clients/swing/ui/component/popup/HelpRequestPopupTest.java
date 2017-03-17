package com.samebug.clients.swing.ui.component.popup;

import com.samebug.clients.common.ui.component.popup.IHelpRequestPopup;
import com.samebug.clients.swing.ui.TestDialog;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;

//@Ignore
public class HelpRequestPopupTest extends TestDialog {
    @Test
    public void popup() {
        waitToInitializeUI("helpRequestPopup/1.json").showDialog();
    }

    public void initializeUI(String resourceJson) throws Exception {
        final IHelpRequestPopup.Model model = readJson(resourceJson, IHelpRequestPopup.Model.class);
        setPreferredSize(new Dimension(320, 180));

        HelpRequestPopup f = new HelpRequestPopup(model);
        setContentPane(f);
    }
}