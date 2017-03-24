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
package com.samebug.clients.swing.ui.modules;

import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public abstract class WebImageService {
    private static WebImageService INSTANCE;

    public static void install(WebImageService instance) {
        assert INSTANCE == null : "WebImageService has already been initialized";
        INSTANCE = instance;
    }

    @NotNull
    public static BufferedImage getAvatarPlaceholder(int width, int height) {
        return INSTANCE.internalGetAvatarPlaceholder(width, height);
    }

    @Nullable
    public static BufferedImage getScaled(@NotNull URL url, int width, int height) {
        return INSTANCE.internalGetScaled(url, width, height);
    }

    @NotNull
    public static BufferedImage[] getSource(@NotNull String iconName, int width, int height) {
        // TODO fallback web source
        URL intellijUrl = IdeaSamebugPlugin.class.getResource("/com/samebug/cache/images/sources/intellij/" + iconName + ".png");
        URL darculaUrl = IdeaSamebugPlugin.class.getResource("/com/samebug/cache/images/sources/darcula/" + iconName + ".png");
        BufferedImage intellijIcon = getScaled(intellijUrl, width, height);
        BufferedImage darculaIcon = getScaled(darculaUrl, width, height);
        return new BufferedImage[]{intellijIcon, darculaIcon};
    }

    @NotNull
    protected abstract BufferedImage internalGetAvatarPlaceholder(int width, int height);

    @Nullable
    protected abstract BufferedImage internalGetScaled(@NotNull URL url, int width, int height);

    protected static final class ScaledKey {
        final URL src;
        final int height;
        final int width;

        public ScaledKey(URL src, int width, int height) {
            this.src = src;
            this.height = height;
            this.width = width;
        }

        @Override
        public int hashCode() {
            return ((31 + src.hashCode()) * 31 + height) * 31 + width;
        }

        @Override
        public boolean equals(Object that) {
            if (that == this) return true;
            else if (!(that instanceof ScaledKey)) return false;
            else {
                final ScaledKey rhs = (ScaledKey) that;
                return rhs.src.equals(src)
                        && rhs.width == width
                        && rhs.height == height;
            }
        }
    }

    // TODO Image.getScaledComponent is told to be evil (slow and bad quality). Not sure if it still holds with Java 7
    // copypasta from https://community.oracle.com/docs/DOC-983611

    /**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img           the original image to be scaled
     * @param targetWidth   the desired width of the scaled instance,
     *                      in pixels
     * @param targetHeight  the desired height of the scaled instance,
     *                      in pixels
     * @param hint          one of the rendering hints that corresponds to
     *                      {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *                      {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *                      {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *                      {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *                      scaling technique that provides higher quality than the usual
     *                      one-step technique (only useful in downscaling cases, where
     *                      {@code targetWidth} or {@code targetHeight} is
     *                      smaller than the original dimensions, and generally only when
     *                      the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    protected static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }
        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }
            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }
            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();
            ret = tmp;
        } while (w != targetWidth || h != targetHeight);
        return ret;
    }
}
