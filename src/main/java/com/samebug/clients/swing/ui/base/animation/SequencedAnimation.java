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

import java.awt.*;

final class SequencedAnimation extends ComponentAnimation {
    private final ComponentAnimation componentAnimation;
    private final LazyComponentAnimation following;
    private ComponentAnimation followingAnimation;
    private boolean isTheFirstOneFinishedYet;

    SequencedAnimation(ComponentAnimation componentAnimation, LazyComponentAnimation following) {
        super(componentAnimation.myTotalFrames + following.myTotalFrames);
        this.componentAnimation = componentAnimation;
        this.following = following;
        isTheFirstOneFinishedYet = false;
    }

    @Override
    protected void doUpdateFrame(int frame) {
        if (frame <= componentAnimation.myTotalFrames) componentAnimation.setFrame(frame);
        if (frame >= componentAnimation.myTotalFrames) {
            if (!isTheFirstOneFinishedYet) {
                isTheFirstOneFinishedYet = true;
                componentAnimation.finish();
                followingAnimation = following.createAnimation();
            }
            followingAnimation.setFrame(frame - componentAnimation.myTotalFrames);
        }
    }

    @Override
    protected void doPaint(Graphics g) {
        if (!isTheFirstOneFinishedYet) componentAnimation.paint(g);
        else followingAnimation.paint(g);
    }

    @Override
    protected void doFinish() {
        if (!isTheFirstOneFinishedYet) componentAnimation.finish();
        followingAnimation.finish();
    }
}
