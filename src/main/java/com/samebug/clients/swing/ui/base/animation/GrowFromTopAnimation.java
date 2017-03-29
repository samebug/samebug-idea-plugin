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
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GrowFromTopAnimation extends ComponentAnimation {
    @NotNull
    protected final JComponent myComponent;
    protected final Dimension myGrownSize;
    protected final int growPixels;
    protected int currentOffset;

    public GrowFromTopAnimation(@NotNull JComponent component, int totalFrames, int growPixels) {
        super(totalFrames);
        this.myComponent = component;
        this.myGrownSize = component.getSize();
        this.growPixels = growPixels;
        assert myGrownSize.height >= growPixels : component + " cannot grow " + growPixels + " pixels because its final size is less than that";

        currentOffset = this.growPixels;
    }

    @Override
    public final void doUpdateFrame(int frame) {
        currentOffset = growPixels - growPixels * frame / myTotalFrames;
        int missingHeight = currentOffset;
        myComponent.setSize(new Dimension(myGrownSize.width, myGrownSize.height - missingHeight));
        myComponent.revalidate();
    }

    @Override
    public final void doPaint(Graphics g) {
        BufferedImage myComponentImage = new BufferedImage(myComponent.getWidth(), myComponent.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = myComponentImage.createGraphics();
        myComponent.paint(graphics);
        graphics.dispose();

        Graphics2D g2 = DrawService.init(g);
        g2.drawImage(myComponentImage,
                0, 0, myComponent.getWidth(), myComponent.getHeight(),
                0, currentOffset, myComponent.getWidth(), currentOffset + myComponent.getHeight(),
                myComponent);
    }
}
