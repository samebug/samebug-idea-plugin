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

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.ref.WeakReference;

public final class ComponentAnimationController {
    @NotNull
    private final IAnimatedComponent myComponent;
    @NotNull
    private WeakReference<PaintableAnimation> currentAnimationRef;

    public ComponentAnimationController(@NotNull IAnimatedComponent myComponent) {
        this.myComponent = myComponent;
        this.currentAnimationRef = new WeakReference<PaintableAnimation>(null);
    }

    public void paint(Graphics g) {
        assert EventQueue.isDispatchThread();
        PaintableAnimation currentAnimation = currentAnimationRef.get();
        if (currentAnimation == null) myComponent.paintOriginalComponent(g);
        else if (!currentAnimation.isRunning()) myComponent.paintOriginalComponent(g);
        else currentAnimation.doPaint(g);
    }

    public void prepareNewAnimation(@NotNull PaintableAnimation animation) {
        assert EventQueue.isDispatchThread();
        PaintableAnimation currentAnimation = currentAnimationRef.get();
        if (currentAnimation != null) currentAnimation.forceFinish();
        currentAnimationRef = new WeakReference<PaintableAnimation>(animation);
    }
}
