/*
 * Copyright 2018 Samebug, Inc.
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

/**
 * IMPROVE check overflows
 */
public final class ChainedAnimation extends ControllableAnimation {
    private ControllableAnimation animationX;
    private ControllableAnimation animationY;
    private int startX;
    private int startY;

    protected ChainedAnimation(ControllableAnimation animationX, ControllableAnimation animationY, int startX, int startY) {
        super(Math.max(startX + animationX.myTotalFrames, startY + animationY.myTotalFrames));
        this.animationX = animationX;
        this.animationY = animationY;
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    protected void doStart() {
        if (startX == 0) animationX.start();
        if (startY == 0) animationY.start();

    }

    @Override
    protected void doSetFrame(int frame) {
        if (!animationX.isStarted() && startX <= frame) animationX.start();
        if (!animationX.isFinished() && startX + animationX.myTotalFrames <= frame) animationX.finish();
        if (animationX.isRunning()) animationX.setFrame(frame - startX);

        if (!animationY.isStarted() && startY <= frame) animationY.start();
        if (!animationY.isFinished() && startY + animationY.myTotalFrames <= frame) animationY.finish();
        if (animationY.isRunning()) animationY.setFrame(frame - startY);
    }

    @Override
    protected void doFinish() {
        if (!animationX.isFinished()) animationX.forceFinish();
        if (!animationY.isFinished()) animationY.forceFinish();
    }
}
