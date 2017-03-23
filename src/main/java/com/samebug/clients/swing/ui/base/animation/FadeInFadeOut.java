package com.samebug.clients.swing.ui.base.animation;

import com.intellij.util.ui.UIUtil;
import com.samebug.clients.swing.ui.modules.DrawService;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

// TODO resize?
public abstract class FadeInFadeOut extends Animator {

    @NotNull
    private final Component myComponent;
    private final BufferedImage myComponentImage;
    private final boolean myFadeIn;
    private double myRatio = 0;

    public FadeInFadeOut(@NotNull Component component, int duration, boolean fadeIn) {
        super("FadeInFadeOut", 10, duration, false, true);
        myComponent = component;
        myFadeIn = fadeIn;
        myRatio = myFadeIn ? 0 : 1;

        myComponentImage = UIUtil.createImage(myComponent.getWidth(), myComponent.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = myComponentImage.createGraphics();
        myComponent.paint(graphics);
        graphics.dispose();
    }

    @Override
    public void paintNow(int frame, int totalFrames, int cycle) {
        double linearProgress = Math.max(0, Math.min(1, (double) frame / totalFrames));
        if (!myFadeIn) linearProgress = 1 - linearProgress;
        myRatio = (1 - Math.cos(Math.PI * linearProgress)) / 2;
        myComponent.repaint();
    }


    public void paint(final Graphics g_) {
        Graphics2D g = DrawService.init(g_);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) myRatio));
        g.drawImage(myComponentImage, 0, 0, myComponent.getWidth(), myComponent.getHeight(), myComponent);
    }
}
