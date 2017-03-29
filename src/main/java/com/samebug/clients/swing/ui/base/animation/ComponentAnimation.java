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

public abstract class ComponentAnimation {
    protected int currentFrame;
    protected boolean finished;
    protected final int myTotalFrames;

    public ComponentAnimation(int totalFrames) {
        this.myTotalFrames = totalFrames;
        this.finished = false;
    }

    // TODO safety (try-catch around doUpdate?)
    // TODO threading guarantees? I think it's safe to assume all of these will be called on EDT.
    // TODO should we fail if it is already finished, or it's ok to silently go on?
    public final void setFrame(int frame) {
        if (!finished) {
            currentFrame = frame;
            doUpdateFrame(frame);
        }
    }

    public final void paint(Graphics g) {
        if (!finished) doPaint(g);
    }

    // TODO guarantees that finish will be called
    public final void finish() {
        if (!finished) {
            doFinish();
            finished = true;
        }
    }

    protected abstract void doUpdateFrame(int frame);

    protected abstract void doPaint(Graphics g);

    protected abstract void doFinish();

    public ComponentAnimation andThen(LazyComponentAnimation following) {
        return new SequencedAnimation(this, following);
    }

    public ComponentAnimation with(ComponentAnimation linkedAnimation) {
        return new ParalelledAnimation(this, linkedAnimation);
    }
}
