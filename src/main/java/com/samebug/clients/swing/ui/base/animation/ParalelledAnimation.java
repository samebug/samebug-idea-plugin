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

final class ParalelledAnimation extends ComponentAnimation {
    private final ComponentAnimation animation1;
    private final ComponentAnimation animation2;

    public ParalelledAnimation(ComponentAnimation animation1, ComponentAnimation animation2) {
        super(animation1.myTotalFrames);
        assert animation1.myTotalFrames == animation2.myTotalFrames : "Parallel animations should have equal length";

        this.animation1 = animation1;
        this.animation2 = animation2;
    }

    @Override
    protected void doUpdateFrame(int frame) {
        animation1.setFrame(frame);
        animation2.setFrame(frame);
    }

    @Override
    protected void doPaint(Graphics g) {
        animation1.paint(g);
        animation2.paint(g);
    }

    @Override
    protected void doFinish() {
        animation1.finish();
        animation2.finish();
    }
}
