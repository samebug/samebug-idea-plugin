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

public final class GradientSampler {

    public static Color[] sample(Color a, Color b, int nPoints) {
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

    private static int linearCombination(int a, int b, int aw, int bw) {
        return (a * aw + b * bw) / (aw + bw);
    }
}
