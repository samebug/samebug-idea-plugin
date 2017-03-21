package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.common.api.form.CreateTip;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.frame.solution.ISolutionFrame;
import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.frame.solution.SolutionFrame;
import com.samebug.clients.swing.ui.modules.ListenerService;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

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

    @Test
    public void activeHelpRequest() {
        waitToInitializeUI("solution/t2w27_helprequest.json").showDialog();
    }

    public void initializeUI(String resourceJson) throws Exception {
        final ISolutionFrame.Model model = readJson(resourceJson, ISolutionFrame.Model.class);

        SolutionFrame f = new SolutionFrame();
        f.loadingSucceeded(model);
        setContentPane(f);

        ListenerService.putListenerToComponent(f, IHelpOthersCTA.Listener.class, new IHelpOthersCTA.Listener() {
            @Override
            public void postTip(IHelpOthersCTA source, String tipBody) {
                ArrayList<FieldError> errors = new ArrayList<FieldError>();
                errors.add(new FieldError(CreateTip.BODY, CreateTip.E_TOO_LONG));
                try {
                    source.failPostTipWithFormError(errors);
                } catch (FormMismatchException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}