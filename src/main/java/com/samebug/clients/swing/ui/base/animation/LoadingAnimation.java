package com.samebug.clients.swing.ui.base.animation;

import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;

import javax.swing.*;
import java.awt.*;

public class LoadingAnimation extends JComponent {
    final int size;
    final MyAnimator animator;
    final Color[] arcColor = ColorService.LoadingArc;

    public LoadingAnimation(int size) {
        this.size = size;
        animator = new MyAnimator();

        Dimension d = new Dimension(size, size);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        animator.resume();
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
            // TODO this should happen on the EDT?
            currentFrame = frame;
            repaint();
        }

        public void paint(Graphics2D g2) {
            int arcCenterDegrees = currentFrame * 10;
            g2.setPaint(gradient(arcCenterDegrees));
            g2.drawOval(1, 1, size - 2, size - 2);
        }

        // TODO we can extract most of the computations to arrays if there is a preformance issue
        private GradientPaint gradient(float arcCenterDegrees) {
            Color tip = ColorService.forCurrentTheme(arcColor);
            Color counterPoint = new Color(tip.getRed(), tip.getGreen(), tip.getBlue(), 0);

            float r = size / 2;
            double arcCenterRadians = (float) Math.toRadians(arcCenterDegrees);
            float sin = (float) Math.sin(arcCenterRadians);
            float cos = (float) Math.cos(arcCenterRadians);
            float arcCenterX = r + sin * r;
            float arcCenterY = r + cos * r;
            float counterPointX = r - sin * r;
            float counterPointY = r - cos * r;
            return new GradientPaint(arcCenterX, arcCenterY, ColorService.forCurrentTheme(arcColor), counterPointX, counterPointY, counterPoint);
        }
    }
}
