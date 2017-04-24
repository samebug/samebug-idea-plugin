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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

public final class WebImageService {
    private static Hashtable<URL, BufferedImage> cache = new Hashtable<URL, BufferedImage>();
    private static Hashtable<ScaledKey, BufferedImage> scaledCache = new Hashtable<ScaledKey, BufferedImage>();
    private static BufferedImage avatarPlaceholder;
    private static BufferedImage[] webSourceIcon;

    public static void install() {
        final URL avatarPlaceholderUrl = WebImageService.class.getResource("/com/samebug/cache/images/avatar-placeholder.png");
        assert avatarPlaceholderUrl != null : "Failed to find avatar placeholder image";
        avatarPlaceholder = getImage(avatarPlaceholderUrl);
        assert avatarPlaceholder != null : "Failed to load avatar placeholder image";

        final URL[] webSourceIconUrl = sourceIconUrl("web");
        assert webSourceIconUrl[0] != null : "Failed to find web source icon for IntelliJ theme";
        assert webSourceIconUrl[1] != null : "Failed to find web source icon for Darcula theme";
        webSourceIcon = new BufferedImage[webSourceIconUrl.length];
        for (int i = 0; i < webSourceIconUrl.length; ++i) {
            webSourceIcon[i] = getImage(webSourceIconUrl[i]);
            assert webSourceIcon[i] != null : "Failed to load web source icon image";
        }
    }

    @NotNull
    public static BufferedImage getAvatar(@Nullable URL avatarUrl, int width, int height) {
        if (avatarUrl == null) return getScaledInstance(avatarPlaceholder, width, height);
        else {
            BufferedImage nonScaledAvatar = getImage(avatarUrl);
            if (nonScaledAvatar == null) nonScaledAvatar = avatarPlaceholder;
            return getScaled(avatarUrl, nonScaledAvatar, width, height);
        }
    }

    @NotNull
    public static BufferedImage[] getSource(@NotNull String iconName, int width, int height) {
        URL[] urls = sourceIconUrl(iconName);
        BufferedImage[] icons = new BufferedImage[urls.length];
        for (int i = 0; i < urls.length; ++i) {
            BufferedImage nonScaled = getImage(urls[i]);
            if (nonScaled == null) nonScaled = webSourceIcon[i];
            icons[i] = getScaled(urls[i], nonScaled, width, height);
        }
        return icons;
    }

    @Nullable
    public static BufferedImage getImage(@NotNull URL url) {
        BufferedImage nonScaled = cache.get(url);
        if (nonScaled == null) {
            try {
                nonScaled = ImageIO.read(url);
                if (nonScaled != null) cache.put(url, nonScaled);
            } catch (IOException e) {
                // IMPROVE smoother handling of load failures.
                // If we failed to download an image, than probably we should note this a failed-to-load url so it won't take much time to render second time.
                // However, to do this, we also have to take care of some expiration, i.e. when should we try again loading this image
                nonScaled = null;
            }
        }
        return nonScaled;
    }

    @NotNull
    private static BufferedImage getScaled(@NotNull URL url, @NotNull BufferedImage nonScaledImage, int width, int height) {
        ScaledKey key = new ScaledKey(url, width, height);
        BufferedImage scaledImage = scaledCache.get(key);
        if (scaledImage == null) {
            scaledImage = getScaledInstance(nonScaledImage, width, height);
            scaledCache.put(key, scaledImage);
            return scaledImage;
        } else {
            return scaledImage;
        }
    }

    private static URL[] sourceIconUrl(String iconName) {
        URL intellijUrl = IdeaSamebugPlugin.class.getResource("/com/samebug/cache/images/sources/intellij/" + iconName + ".png");
        URL darculaUrl = IdeaSamebugPlugin.class.getResource("/com/samebug/cache/images/sources/darcula/" + iconName + ".png");
        return new URL[]{intellijUrl, darculaUrl};
    }

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

    // IMPROVE Image.getScaledComponent is told to be evil (slow and bad quality). Not sure if it still holds with Java 7
    // copypasta from https://community.oracle.com/docs/DOC-983611

    /**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img          the original image to be scaled
     * @param targetWidth  the desired width of the scaled instance,
     *                     in pixels
     * @param targetHeight the desired height of the scaled instance,
     *                     in pixels
     * @return a scaled version of the original {@code BufferedImage}
     */
    @NotNull
    protected static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight) {
        /**
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
         */
        Object hint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        boolean higherQuality = true;
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
            // scale down in multiple passes with drawImage()
            // Use multi-step technique: start with original size, then
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
