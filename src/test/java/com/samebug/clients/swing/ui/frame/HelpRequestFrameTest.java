package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.frame.helpRequest.HelpRequestFrame;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class HelpRequestFrameTest extends TestDialog {
    @Test
    public void zeroTipsAndZeroWebHits() {
        waitToInitializeUI("helpRequest/t0w0.json").showDialog();
    }

    @Test
    public void twoTipsAndTwoWebHits() {
        waitToInitializeUI("helpRequest/t2w2.json").showDialog();
    }

    protected void initializeUI(String resourceJson) throws Exception {
        final HelpRequestFrame.Model model = readJson(resourceJson, HelpRequestFrame.Model.class);

        HelpRequestFrame f = new HelpRequestFrame();
        f.loadingSucceeded(model);
        setContentPane(f);
    }
}
