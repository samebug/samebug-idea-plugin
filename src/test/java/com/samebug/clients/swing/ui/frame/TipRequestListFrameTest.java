package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.frame.tipRequestList.TipRequestListFrame;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TipRequestListFrameTest extends TestDialog {
    @Test
    public void empty() {
        waitToInitializeUI("tipRequestList/0.json").showDialog();
    }

    @Test
    public void two() {
        waitToInitializeUI("tipRequestList/2.json").showDialog();
    }

    @Test
    public void twenty() {
        waitToInitializeUI("tipRequestList/20.json").showDialog();
    }

    protected void initializeUI(String resourceJson) throws Exception {
        final TipRequestListFrame.Model model = readJson(resourceJson, TipRequestListFrame.Model.class);

        TipRequestListFrame f = new TipRequestListFrame();
        f.loadingSucceeded(model);
        setContentPane(f);
    }
}
