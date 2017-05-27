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
import java.util.ArrayList;
import java.util.List;

public abstract class ControllableAnimation {
    protected final int myTotalFrames;
    protected int myCurrentFrame;
    protected State myState;
    private final List<Runnable> runBeforeStart;
    private final List<Runnable> runAfterStart;
    private final List<Runnable> runBeforeFinish;
    private final List<Runnable> runAfterFinish;

    protected ControllableAnimation(int totalFrames) {
        assert totalFrames >= 2 : "Animations should have at least two frames";
        myTotalFrames = totalFrames;
        myCurrentFrame = -1;
        myState = State.INITIALIZED;
        runAfterStart = new ArrayList<Runnable>(0);
        runBeforeStart = new ArrayList<Runnable>(4);
        runAfterFinish = new ArrayList<Runnable>(0);
        runBeforeFinish = new ArrayList<Runnable>(0);
    }

    public final void start() {
        assert myState == State.INITIALIZED : "Animation started from state " + myState;
        assert EventQueue.isDispatchThread();

        // TODO running befores are reversed. this is all crap
        for (int i = runBeforeStart.size() - 1; i >= 0; --i) runBeforeStart.get(i).run();
        myState = State.RUNNING;
        myCurrentFrame = 0;
        doStart();
        for (Runnable r : runAfterStart) r.run();
    }

    public final void setFrame(int frame) {
        assert myState == State.RUNNING : "Animation updated in state " + myState;
        assert EventQueue.isDispatchThread();
        assert frame >= 0 : "Invalid frame number " + frame;
        assert frame < myTotalFrames : "Invalid frame number " + frame;

        myCurrentFrame = frame;
        doSetFrame(frame);
    }

    public final void finish() {
        assert myState == State.RUNNING : "Animation finished at state " + myState;
        assert EventQueue.isDispatchThread();

        for (Runnable r : runBeforeFinish) r.run();
        myState = State.FINISHED;
        myCurrentFrame = myTotalFrames;
        doFinish();
        for (Runnable r : runAfterFinish) r.run();
    }

    public final boolean isStarted() {
        return myState != State.INITIALIZED;
    }

    public final boolean isRunning() {
        return myState == State.RUNNING;
    }

    public final boolean isFinished() {
        return myState == State.FINISHED;
    }

    public final void forceFinish() {
        // intentional fallthrough
        switch (myState) {
            case INITIALIZED:
                start();
            case RUNNING:
                finish();
            case FINISHED:
            default:
        }
    }

    public final void runBeforeStart(Runnable work) {
        assert !isStarted() : "Adding task to animation start when it is already in state " + myState;
        runBeforeStart.add(work);
    }

    protected abstract void doStart();

    protected abstract void doSetFrame(int frame);

    protected abstract void doFinish();

    public ControllableAnimation with(ControllableAnimation linkedAnimation) {
        return new ChainedAnimation(this, linkedAnimation, 0, 0);
    }

    public ControllableAnimation andThen(ControllableAnimation following) {
        return new ChainedAnimation(this, following, 0, myTotalFrames);
    }

    public ControllableAnimation andThen(ControllableAnimation following, int frameOffset) {
        return new ChainedAnimation(this, following, 0, frameOffset);
    }

    public enum State {
        INITIALIZED, RUNNING, FINISHED
    }
}
