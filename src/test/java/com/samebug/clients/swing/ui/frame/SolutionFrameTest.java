package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.common.ui.frame.solution.ISolutionFrame;
import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.frame.solution.SolutionFrame;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class SolutionFrameTest extends TestDialog {
    @Test
    public void everythingIsEmpty() {
        waitToInitializeUI("solution/empty.json").showDialog();
    }

    @Test
    public void oneBugmate() {
        waitToInitializeUI("solution/b1.json").showDialog();
    }
    @Test
    public void twoBugmates() {
        waitToInitializeUI("solution/b2.json").showDialog();
    }
    @Test
    public void threeBugmates() {
        waitToInitializeUI("solution/b3.json").showDialog();
    }
    @Test
    public void zeroTipsAndZeroWebHits() {
        waitToInitializeUI("solution/t0w0.json").showDialog();
    }
    @Test
    public void twoTipsAndTwentysevenWebHits() {
        waitToInitializeUI("solution/t2w27.json").showDialog();
    }

    public void initializeUI(String resourceJson) throws Exception {
        final ISolutionFrame.Model model = readJson(resourceJson, ISolutionFrame.Model.class);

        SolutionFrame f = new SolutionFrame();
        f.loadingSucceeded(model);
        setContentPane(f);
    }
}