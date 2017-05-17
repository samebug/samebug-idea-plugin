/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.base.animation;

import com.samebug.clients.swing.ui.modules.DrawService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ShrinkAwayAnimation extends PaintableAnimation {
    protected final JComponent myComponent;
    protected BufferedImage myComponentImage;
    protected Dimension myShrinkedSize;
    protected final int shrinkPixels;
    protected final int[] offsets;
    protected int currentOffset;
    protected double myRatio = 0;

    protected ShrinkAwayAnimation(int totalFrames, final JComponent myComponent, final int shrinkPixels) {
        super(totalFrames);
        this.myComponent = myComponent;
        this.shrinkPixels = shrinkPixels;
        assert shrinkPixels > 0 : "Cannot shrink " + shrinkPixels + " pixels, that's negative";
        this.offsets = Sampler.easeInOutCubic(shrinkPixels, totalFrames);
        runBeforeStart(new Runnable() {
            @Override
            public void run() {
                // TODO does this work properly on Retina?
                // in the intellij code they used  myComponentImage = UIUtil.createImage(myComponent.getWidth(), myComponent.getHeight(), BufferedImage.TYPE_INT_ARGB);
                myShrinkedSize = new Dimension(myComponent.getPreferredSize().width, myComponent.getPreferredSize().height - shrinkPixels);
                assert myShrinkedSize.height > 0 : "Cannot shrink " + shrinkPixels + " pixels, the component is smaller";
                myComponentImage = new BufferedImage(myComponent.getWidth(), myComponent.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = myComponentImage.createGraphics();
                myComponent.paint(graphics);
                graphics.dispose();
            }
        });
    }

    @Override
    protected void doStart() {
        currentOffset = this.shrinkPixels;
        myComponent.setSize(new Dimension(myShrinkedSize.width, myShrinkedSize.height + currentOffset));
        myComponent.revalidate();
    }

    @Override
    public final void doSetFrame(int frame) {
        double linearProgress = 1 - Math.max(0, Math.min(1, (double) frame / myTotalFrames));
        myRatio = (1 - Math.cos(Math.PI * linearProgress)) / 2;

        currentOffset = shrinkPixels - offsets[frame];
        myComponent.setMinimumSize(new Dimension(myShrinkedSize.width, myShrinkedSize.height + currentOffset));
        myComponent.setPreferredSize(new Dimension(myShrinkedSize.width, myShrinkedSize.height + currentOffset));
        myComponent.setMaximumSize(new Dimension(myShrinkedSize.width, myShrinkedSize.height + currentOffset));
        myComponent.setSize(new Dimension(myShrinkedSize.width, myShrinkedSize.height + currentOffset));
        myComponent.revalidate();
    }

    @Override
    public final void doPaint(Graphics g) {
        Graphics2D g2 = DrawService.init(g);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) myRatio));
        g2.drawImage(myComponentImage,
                0, 0, myComponent.getPreferredSize().width, myComponent.getPreferredSize().height,
                0, 0, myComponent.getPreferredSize().width, myComponent.getPreferredSize().height,
                myComponent);
    }
}
