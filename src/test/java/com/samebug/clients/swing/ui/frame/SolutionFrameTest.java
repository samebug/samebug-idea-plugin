package com.samebug.clients.swing.ui.frame;

import com.samebug.clients.common.api.form.CreateTip;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.frame.solution.ISolutionFrame;
import com.samebug.clients.swing.ui.TestDialog;
import com.samebug.clients.swing.ui.component.community.writeTip.WriteTip;
import com.samebug.clients.swing.ui.frame.solution.ResultTabs;
import com.samebug.clients.swing.ui.frame.solution.SolutionFrame;
import com.samebug.clients.swing.ui.frame.solution.TipResultsTab;
import com.samebug.clients.swing.ui.frame.solution.WebResultsTab;
import com.samebug.clients.swing.ui.modules.ListenerService;
import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

//@Ignore
public class SolutionFrameTest extends TestDialog {
//    @Test
//    public void everythingIsEmpty() {
//        waitToInitializeUI("solution/empty.json").showDialog();
//    }
//
//    @Test
//    public void oneBugmate() {
//        waitToInitializeUI("solution/b1.json").showDialog();
//    }
//
//    @Test
//    public void twoBugmates() {
//        waitToInitializeUI("solution/b2.json").showDialog();
//    }
//
//    @Test
//    public void threeBugmates() {
//        waitToInitializeUI("solution/b3.json").showDialog();
//    }

    @Test
    public void zeroTipsAndZeroWebHits() {
        waitToInitializeUI("solution/t0w0.json").showDialog();
    }

//    @Test
//    public void twoTipsAndTwentysevenWebHits() {
//        waitToInitializeUI("solution/t2w27.json").showDialog();
//    }
//
//    @Test
//    public void activeHelpRequest() {
//        waitToInitializeUI("solution/t2w27_helprequest.json").showDialog();
//    }

    public void initializeUI(String resourceJson) throws Exception {
        final ISolutionFrame.Model model = readJson(resourceJson, ISolutionFrame.Model.class);

        SolutionFrame f = new SolutionFrame();
        f.loadingSucceeded(model);
        setContentPane(f);

        ListenerService.putListenerToComponent(f, IHelpOthersCTA.Listener.class, new IHelpOthersCTA.Listener() {
            @Override
            public void postTip(IHelpOthersCTA source, String tipBody) {
                // TODO can be helpRequest
                WriteTip writeTip = findAncestor((Component) source, WriteTip.class);
                ResultTabs resultTabs = findAncestor(writeTip, ResultTabs.class);

                resultTabs.animatedAddTip(new ITipHit.Model(tipBody, 0, new Date(), "me", null,
                        new IMarkButton.Model(0, 0, true)));
            }
        });
    }


    private static <T> T findAncestor(Component component, Class<T> searchedClass) {
        for (Component c = component; c != null; c = c.getParent()) {
            if (searchedClass.isInstance(c)) return searchedClass.cast(c);
        }
        return null;
    }
}