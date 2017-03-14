package com.samebug.clients.swing.ui.base.button;

import com.samebug.clients.swing.ui.base.animation.LoadingAnimation;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public abstract class ActionButton extends BasicButton {
    protected boolean loading;
    protected LoadingAnimation loadingAnimation;
    // TODO hack to keep the state during the loading animation
    protected boolean filledState;

    public ActionButton() {
        this(true);
    }

    public ActionButton(boolean filled) {
        super(filled);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    // override this if necessary, but you should still call super.changeToLoadingAnimation()
    public void changeToLoadingAnimation() {
        // TODO property change
        loading = true;
        setEnabled(false);
        filledState = isFilled();
        setFilled(false);
        // TODO use current size instead of hard coded size
        Dimension currentSize = getSize();

        removeAll();
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setLayout(new MigLayout("", ":push[]:push", ":push[]:push"));
        loadingAnimation = new LoadingAnimation(20);
        add(loadingAnimation, "align center, h 20!");
        setPreferredSize(currentSize);
        revalidate();
        repaint();
    }

    // override this if necessary, but you should still call super.revertFromLoadingAnimation()
    public void revertFromLoadingAnimation() {
        loading = false;
        setFilled(filledState);
        setEnabled(true);
        removeAll();
        // TODO destroy animation to free stop the task?
    }

    ;
}
