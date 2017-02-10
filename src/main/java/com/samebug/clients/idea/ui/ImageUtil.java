/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;

final public class ImageUtil {
    private static final Logger LOGGER = Logger.getInstance(ImageUtil.class);
    private static final Hashtable<URL, BufferedImage> cache = new Hashtable<URL, BufferedImage>();
    private static final Hashtable<ScaledKey, BufferedImage> scaledCache = new Hashtable<ScaledKey, BufferedImage>();
    private static final URL avatarPlaceholderUrl;
    private static final BufferedImage avatarPlaceholder;

    @Nullable
    public static Image getAvatarPlaceholder() {
        return avatarPlaceholder;
    }

    @Nullable
    public static BufferedImage getAvatarPlaceholder(int width, int height) {
        return getScaledThroughCache(avatarPlaceholderUrl, avatarPlaceholder, width, height);
    }

    @Nullable
    public static BufferedImage get(@NotNull URL url) {
        return cache.get(url);
    }

    @Nullable
    public static BufferedImage getScaled(@NotNull URL url, int width, int height) {
        return getScaledThroughCache(url, cache.get(url), width, height);
    }

    @Nullable
    private static BufferedImage getScaledThroughCache(@NotNull URL url, BufferedImage nonScaledImage, int width, int height) {
        ScaledKey key = new ScaledKey(url, width, height);
        BufferedImage scaledImage = scaledCache.get(key);
        if (scaledImage == null) {
            if (nonScaledImage == null) {
                return null;
            } else {
                scaledImage = getScaledInstance(nonScaledImage, width, height, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, true);
                scaledCache.put(key, scaledImage);
                return scaledImage;
            }
        } else {
            return scaledImage;
        }
    }

    public static void loadImages(@NotNull final Collection<URL> sources) {
        for (final URL url : sources) {
            if (url == null || cache.get(url) != null) continue;
            try {
                BufferedImage image = ImageIO.read(url);
                if (image != null) cache.put(url, image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static final String[] cachedImages = {
            "images/sources/alfresco.png",
            "images/sources/apache.png",
            "images/sources/appcelerator.png",
            "images/sources/appfuse.png",
            "images/sources/atlassian.png",
            "images/sources/cask.png",
            "images/sources/cloudera.png",
            "images/sources/codehaus.png",
            "images/sources/couchbase.png",
            "images/sources/datastax.png",
            "images/sources/djigzo.png",
            "images/sources/duraspace.png",
            "images/sources/ehour.png",
            "images/sources/forgerock.png",
            "images/sources/github.png",
            "images/sources/googlegroups.png",
            "images/sources/gradle.png",
            "images/sources/grails.png",
            "images/sources/hibernate.png",
            "images/sources/icesoft.png",
            "images/sources/igniterealtime.png",
            "images/sources/internet2.png",
            "images/sources/jasig.png",
            "images/sources/java.png",
            "images/sources/jboss.png",
            "images/sources/jenkins.png",
            "images/sources/jfrog.png",
            "images/sources/jira.png",
            "images/sources/kuali.png",
            "images/sources/liferay.png",
            "images/sources/mirthcorp.png",
            "images/sources/mojang.png",
            "images/sources/mulesoft.png",
            "images/sources/openjdk.png",
            "images/sources/opennms.png",
            "images/sources/page.png",
            "images/sources/pentaho.png",
            "images/sources/pmease.png",
            "images/sources/qt.png",
            "images/sources/sakai.png",
            "images/sources/samebug.png",
            "images/sources/samebug-justbug.png",
            "images/sources/scala.png",
            "images/sources/sonarsource.png",
            "images/sources/sonatype.png",
            "images/sources/sourceforge.png",
            "images/sources/spring.png",
            "images/sources/springsource.png",
            "images/sources/stackoverflow.png",
            "images/sources/talendforge.png",
            "images/sources/terracotta.png",
            "images/sources/web.png",
            "images/sources/wso2.png",
            "images/sources/xwiki.png",
            "images/sources/youtrack.png",
            "images/sources/zkoss.png"
    };

    static {
        URL tmpAvatarUrl = null;
        BufferedImage tmpAvatarImage = null;
        try {
            tmpAvatarUrl = ImageUtil.class.getResource("/com/samebug/cache/images/avatar-placeholder.png");
            if (tmpAvatarUrl != null) tmpAvatarImage = ImageIO.read(tmpAvatarUrl);
        } catch (IOException e) {
            LOGGER.warn("Failed to read avatar-placeholder.png as an image!", e);
        }
        avatarPlaceholderUrl = tmpAvatarUrl;
        avatarPlaceholder = tmpAvatarImage;

        for (String imageUri : cachedImages) {
            InputStream imageBytes = IdeaSamebugPlugin.class.getResourceAsStream("/com/samebug/cache/" + imageUri);
            if (imageBytes == null) {
                LOGGER.warn("Image " + imageUri + " was not found!");
            } else {
                try {
                    URL remoteUrl = IdeaSamebugPlugin.getInstance().getUrlBuilder().assets(imageUri);
                    BufferedImage image = ImageIO.read(imageBytes);
                    cache.put(remoteUrl, image);
                } catch (MalformedURLException e) {
                    LOGGER.warn("Url of image " + imageUri + " could not be resolved!", e);
                } catch (IOException e) {
                    LOGGER.warn("Failed to read " + imageUri + " as an image!", e);
                }
            }
        }
    }

    private static final class ScaledKey {
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
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     * in pixels
     * @param targetHeight the desired height of the scaled instance,
     * in pixels
     * @param hint one of the rendering hints that corresponds to
     * {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     * {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     * {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     * {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     * scaling technique that provides higher quality than the usual
     * one-step technique (only useful in downscaling cases, where
     * {@code targetWidth} or {@code targetHeight} is
     * smaller than the original dimensions, and generally only when
     * the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    private static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
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
