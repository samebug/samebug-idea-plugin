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

import com.intellij.ui.AncestorListenerAdapter;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.*;

public class LoadingAnimation extends JComponent {
    final int size;
    final int thickness;
    final MyAnimator animator;
    final Color arcColor;

    public LoadingAnimation(int size) {
        this(size, ColorService.forCurrentTheme(ColorService.LoadingArc));
    }

    public LoadingAnimation(int size, Color arcColor) {
        this.size = size;
        this.arcColor = arcColor;
        thickness = size < 20 ? 1 : size / 20;
        animator = new MyAnimator();

        Dimension d = new Dimension(size, size);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                animator.resume();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                animator.suspend();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = DrawService.init(g);
        animator.paint(g2);
    }

    private class MyAnimator extends Animator {
        private int currentFrame;

        public MyAnimator() {
            super("loading", 36, 1000, true);
        }

        @Override
        public void paintNow(int frame, int totalFrames, int cycle) {
            currentFrame = frame;
            repaint();
        }

        public void paint(Graphics2D g2) {
            int arcCenterDegrees = -currentFrame * 10;
            g2.setStroke(new BasicStroke(thickness));
            g2.setPaint(gradient(arcCenterDegrees));
            g2.drawOval(thickness / 2, thickness / 2, size - 2 * (thickness / 2) - 1, size - 2 * (thickness / 2) - 1);
        }

        // TODO we can extract most of the computations to arrays if there is a preformance issue
        private GradientPaint gradient(float arcCenterDegrees) {
            Color counterPoint = new Color(arcColor.getRed(), arcColor.getGreen(), arcColor.getBlue(), 0);

            float r = size / 2;
            double arcCenterRadians = (float) Math.toRadians(arcCenterDegrees);
            float sin = (float) Math.sin(arcCenterRadians);
            float cos = (float) Math.cos(arcCenterRadians);
            float arcCenterX = r + sin * r;
            float arcCenterY = r + cos * r;
            float counterPointX = r - sin * r;
            float counterPointY = r - cos * r;
            return new GradientPaint(arcCenterX, arcCenterY, arcColor, counterPointX, counterPointY, counterPoint);
        }
    }
}
