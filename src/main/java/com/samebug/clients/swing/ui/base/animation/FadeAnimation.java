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

abstract class FadeAnimation extends ComponentAnimation {
    protected final JComponent myComponent;
    protected BufferedImage myComponentImage;
    private final boolean myFadeIn;
    protected double myRatio = 0;

    public FadeAnimation(JComponent component, int totalFrames, boolean fadeIn) {
        super(totalFrames);
        myComponent = component;
        myFadeIn = fadeIn;
        runBeforeStart(new Runnable() {
            @Override
            public void run() {
                myComponent.validate();
                // TODO does this work properly on Retina?
                // in the intellij code they used  myComponentImage = UIUtil.createImage(myComponent.getWidth(), myComponent.getHeight(), BufferedImage.TYPE_INT_ARGB);
                myComponentImage = new BufferedImage(myComponent.getWidth(), myComponent.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = myComponentImage.createGraphics();
                myComponent.paint(graphics);
                graphics.dispose();
            }
        });
    }

    @Override
    public final void doStart() {
    }

    @Override
    public final void doSetFrame(int frame) {
        double linearProgress = Math.max(0, Math.min(1, (double) frame / myTotalFrames));
        if (!myFadeIn) linearProgress = 1 - linearProgress;
        myRatio = (1 - Math.cos(Math.PI * linearProgress)) / 2;
    }

    @Override
    public final void doPaint(Graphics g) {
        Graphics2D g2 = DrawService.init(g);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) myRatio));
        g2.drawImage(myComponentImage, 0, 0, myComponent.getWidth(), myComponent.getHeight(), myComponent);
    }
}
