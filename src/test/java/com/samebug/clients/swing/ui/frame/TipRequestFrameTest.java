package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.frame.tipRequest.TipRequestFrame;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TipRequestFrameTest extends TestDialog {
    @Test
    public void zeroTipsAndZeroWebHits() {
        waitToInitializeUI("tipRequest/t0w0.json").showDialog();
    }

    @Test
    public void twoTipsAndTwoWebHits() {
        waitToInitializeUI("tipRequest/t2w2.json").showDialog();
    }

    protected void initializeUI(String resourceJson) throws Exception {
        final TipRequestFrame.Model model = readJson(resourceJson, TipRequestFrame.Model.class);

        TipRequestFrame f = new TipRequestFrame();
        f.loadingSucceeded(model);
        setContentPane(f);
    }
}