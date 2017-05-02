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

public final class Sampler {

    public static Color[] gradient(Color a, Color b, int nPoints) {
        assert nPoints >= 2;
        Color[] gradient = new Color[nPoints];
        for (int i = 0; i < nPoints; ++i) {
            int aWeight = nPoints - 1 - i;
            int bWeight = i;
            gradient[i] = new Color(
                    linearCombination(a.getRed(), b.getRed(), aWeight, bWeight),
                    linearCombination(a.getGreen(), b.getGreen(), aWeight, bWeight),
                    linearCombination(a.getBlue(), b.getBlue(), aWeight, bWeight),
                    linearCombination(a.getAlpha(), b.getAlpha(), aWeight, bWeight)
            );
        }
        return gradient;
    }

    public static int[] easeInOutCubic(int pixels, int nPoints) {
        assert nPoints >= 2;
        int[] sample = new int[nPoints];
        double total = (double) (nPoints - 1);
        for (int i = 0; i < nPoints; ++i) {
            sample[i] = (int) (pixels * easeInOutCubic(i / total));
        }
        return sample;
    }

    public static int linearCombination(int a, int b, int aw, int bw) {
        return (a * aw + b * bw) / (aw + bw);
    }

    public static double easeInOutCubic(double t) {
        return 0d * Math.pow(1 - t, 3) + 3 * .045d * Math.pow(1 - t, 2) * t + 3 * 1d * (1 - t) * Math.pow(t, 2) + 1d * Math.pow(t, 3);
    }

    private Sampler() {}
}
