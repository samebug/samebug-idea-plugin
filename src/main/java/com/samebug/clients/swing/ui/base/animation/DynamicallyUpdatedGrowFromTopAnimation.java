package com.samebug.clients.swing.ui.base.animation;

import com.samebug.clients.swing.ui.modules.DrawService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class DynamicallyUpdatedGrowFromTopAnimation extends ComponentAnimation {
    protected final JComponent myComponent;
    protected final Dimension myGrownSize;
    protected final int growPixels;
    protected final int[] offsets;
    protected int currentOffset;

    protected DynamicallyUpdatedGrowFromTopAnimation(int totalFrames, JComponent myComponent, int growPixels) {
        super(totalFrames);
        this.myComponent = myComponent;
        this.myGrownSize = myComponent.getSize();
        this.growPixels = growPixels;
        assert myGrownSize.height >= growPixels : "Cannot grow " + growPixels + " pixels because its final size is less than that";
        this.offsets = Sampler.easeInOutCubic(growPixels, totalFrames);
    }

    @Override
    protected void doStart() {
        currentOffset = this.growPixels;
        myComponent.setSize(new Dimension(myGrownSize.width, myGrownSize.height - currentOffset));
        myComponent.revalidate();
    }

    @Override
    public final void doSetFrame(int frame) {
        currentOffset = growPixels - offsets[frame];
        myComponent.setSize(new Dimension(myGrownSize.width, myGrownSize.height - currentOffset));
        myComponent.revalidate();
    }

    @Override
    public final void doPaint(Graphics g) {
        // IMPROVE probably it can be done without writing to image first, using clipRect?
        BufferedImage myComponentImage = new BufferedImage(myComponent.getWidth(), myComponent.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = myComponentImage.createGraphics();
        // TODO this is hacky, changing away from running state so the owner component will render normally (ignoring the animation)
        myState = State.INITIALIZED;
        myComponent.paint(graphics);
        myState = State.RUNNING;
        graphics.dispose();

        Graphics2D g2 = DrawService.init(g);
        g2.drawImage(myComponentImage,
                0, 0, myComponent.getWidth(), myComponent.getHeight(),
                0, currentOffset, myComponent.getWidth(), currentOffset + myComponent.getHeight(),
                myComponent);
    }

}
