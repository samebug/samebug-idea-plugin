package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.frame.helpRequestList.HelpRequestListFrame;
import org.junit.Test;

public class HelpRequestListFrameTest extends TestDialog {
    @Test
    public void empty() {
        waitToInitializeUI("helpRequestList/0.json").showDialog();
    }

    @Test
    public void two() {
        waitToInitializeUI("helpRequestList/2.json").showDialog();
    }

    @Test
    public void twenty() {
        waitToInitializeUI("helpRequestList/20.json").showDialog();
    }

    protected void initializeUI(String resourceJson) throws Exception {
        final HelpRequestListFrame.Model model = readJson(resourceJson, HelpRequestListFrame.Model.class);

        HelpRequestListFrame f = new HelpRequestListFrame();
        f.loadingSucceeded(model);
        setContentPane(f);
    }
}
