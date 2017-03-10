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
package com.samebug.clients.swing.ui.testModules;

import com.samebug.clients.swing.ui.modules.WebImageService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;

public final class TestWebImageService extends WebImageService {
    private final Hashtable<URL, BufferedImage> cache = new Hashtable<URL, BufferedImage>();
    private final Hashtable<ScaledKey, BufferedImage> scaledCache = new Hashtable<ScaledKey, BufferedImage>();
    private final URL avatarPlaceholderUrl;
    private final BufferedImage avatarPlaceholder;

    @Override
    protected void internalLoadImages(@NotNull Collection<URL> sources) {

    }

    @Override
    protected BufferedImage internalGetScaled(@NotNull URL url, int width, int height) {
        BufferedImage nonScaled = cache.get(url);
        if (nonScaled == null) {
            try {
                nonScaled = ImageIO.read(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getScaledThroughCache(url, nonScaled, width, height);
    }

    @Override
    protected BufferedImage internalGet(@NotNull URL url) {
        return cache.get(url);
    }

    @Override
    protected BufferedImage internalGetAvatarPlaceholder(int width, int height) {
        return getScaledThroughCache(avatarPlaceholderUrl, avatarPlaceholder, width, height);
    }

    @Override
    protected Image internalGetAvatarPlaceholder() {
        return avatarPlaceholder;
    }

    @Nullable
    private BufferedImage getScaledThroughCache(@NotNull URL url, BufferedImage nonScaledImage, int width, int height) {
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

    {
        URL tmpAvatarUrl = null;
        BufferedImage tmpAvatarImage = null;
        try {
            tmpAvatarUrl = WebImageService.class.getResource("/com/samebug/cache/images/avatar-placeholder.png");
            if (tmpAvatarUrl != null) tmpAvatarImage = ImageIO.read(tmpAvatarUrl);
        } catch (IOException e) {
            System.err.println("Failed to read avatar-placeholder.png as an image!");
            e.printStackTrace();
        }
        avatarPlaceholderUrl = tmpAvatarUrl;
        avatarPlaceholder = tmpAvatarImage;

        for (String imageUri : cachedImages) {
            InputStream imageBytes = WebImageService.class.getResourceAsStream("/com/samebug/cache/" + imageUri);
            if (imageBytes == null) {
                System.err.println("Image " + imageUri + " was not found!");
            } else {
                try {
                    URL remoteUrl = URI.create("https://samebug.io/assets/").resolve(imageUri).toURL();
                    BufferedImage image = ImageIO.read(imageBytes);
                    cache.put(remoteUrl, image);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("Failed to read " + imageUri + " as an image!");
                    e.printStackTrace();
                }
            }
        }
    }
}
