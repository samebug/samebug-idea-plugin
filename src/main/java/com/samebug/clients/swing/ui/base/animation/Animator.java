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
import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Copied from com.intellij.util.ui.Animator
 */
// TODO isn't there a better solution where we don't have to destory it after usage?
public abstract class Animator {
    private final String myName;
    private final int myTotalFrames;
    private final int myCycleDuration;
    private final boolean myForward;
    private final boolean myRepeatable;

    private ScheduledFuture<?> myTicker;

    private int myCurrentFrame;
    private long myStartTime;
    private long myStartDeltaTime;
    private boolean myInitialStep;

    // TODO
    private static final ScheduledExecutorService animatorExecutorService = Executors.newScheduledThreadPool(1);

    public Animator(final String name, final int totalFrames, final int cycleDuration, boolean repeatable) {
        this(name, totalFrames, cycleDuration, repeatable, true);
    }

    public Animator(final String name, final int totalFrames, final int cycleDuration, boolean repeatable, boolean forward) {
        myName = name;
        myTotalFrames = totalFrames;
        myCycleDuration = cycleDuration;
        myRepeatable = repeatable;
        myForward = forward;

        reset();
    }

    private void onTick() {
        if (myInitialStep) {
            myInitialStep = false;
            myStartTime = System.currentTimeMillis() - myStartDeltaTime; // keep animation state on suspend
            paint();
            return;
        }

        double cycleTime = System.currentTimeMillis() - myStartTime;
        if (cycleTime < 0) return; // currentTimeMillis() is not monotonic - let's pretend that animation didn't changed

        long newFrame = (long) (cycleTime * myTotalFrames / myCycleDuration);

        if (myRepeatable) {
            newFrame %= myTotalFrames;
        }

        if (newFrame == myCurrentFrame) return;

        if (!myRepeatable && newFrame >= myTotalFrames) {
            animationDone();
            return;
        }

        myCurrentFrame = (int) newFrame;

        paint();
    }

    private void paint() {
        paintNow(myForward ? myCurrentFrame : myTotalFrames - myCurrentFrame - 1, myTotalFrames, myCycleDuration);
        // NOTE seems to be required on linux as window managers less frequently render the frame if there is no interaction
        // see https://docs.oracle.com/javase/7/docs/api/java/awt/Toolkit.html#sync()
        Toolkit.getDefaultToolkit().sync();

    }

    private void animationDone() {
        stopTicker();
    }

    private void stopTicker() {
        if (myTicker != null) {
            myTicker.cancel(false);
            myTicker = null;
        }
    }

    public void suspend() {
        myStartDeltaTime = System.currentTimeMillis() - myStartTime;
        myInitialStep = true;
        stopTicker();
    }

    public void resume() {
        if (myCycleDuration == 0) {
            myCurrentFrame = myTotalFrames - 1;
            paint();
            animationDone();
        } else if (myTicker == null) {
            myTicker = animatorExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    onTick();
                }

                @Override
                public String toString() {
                    return "Scheduled " + Animator.this;
                }
            }, 0, myCycleDuration * 1000 / myTotalFrames, TimeUnit.MICROSECONDS);
        }
    }

    public abstract void paintNow(int frame, int totalFrames, int cycle);

    public boolean isRunning() {
        return myTicker != null;
    }

    public void reset() {
        myCurrentFrame = 0;
        myStartDeltaTime = 0;
        myInitialStep = true;
    }

    public final boolean isForward() {
        return myForward;
    }

    @Override
    public String toString() {
        ScheduledFuture<?> future = myTicker;
        String state = future == null || future.isDone() ? "stopped" : "running " + myCurrentFrame + "/" + myTotalFrames + " frame";
        return MessageFormat.format("Animator '{0}' @{1} ({2})", myName, System.identityHashCode(this), state);
    }
}
