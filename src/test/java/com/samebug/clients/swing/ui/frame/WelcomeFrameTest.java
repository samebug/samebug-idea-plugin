package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.frame.welcome.WelcomeFrame;
import org.junit.Test;

public class WelcomeFrameTest extends TestDialog {
    @Test
    public void empty() {
        waitToInitializeUI("welcome/0.json").showDialog();
    }

    protected void initializeUI(String resourceJson) throws Exception {
        final WelcomeFrame.Model model = readJson(resourceJson, WelcomeFrame.Model.class);

        WelcomeFrame f = new WelcomeFrame();
        f.loadingSucceeded(model);
        setContentPane(f);
    }
}
