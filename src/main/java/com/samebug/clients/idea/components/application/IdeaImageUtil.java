package com.samebug.clients.idea.components.application;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.search.api.WebUrlBuilder;
import com.samebug.clients.swing.ui.ImageUtil;
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

final class IdeaImageUtil extends ImageUtil {
    private final Logger LOGGER = Logger.getInstance(IdeaImageUtil.class);
    private final Hashtable<URL, BufferedImage> cache = new Hashtable<URL, BufferedImage>();
    private final Hashtable<ScaledKey, BufferedImage> scaledCache = new Hashtable<ScaledKey, BufferedImage>();
    private final URL avatarPlaceholderUrl;
    private final BufferedImage avatarPlaceholder;

    private final WebUrlBuilder urlBuilder;

    @Override
    @Nullable
    protected Image _getAvatarPlaceholder() {
        return avatarPlaceholder;
    }

    @Override
    @Nullable
    public BufferedImage _getAvatarPlaceholder(int width, int height) {
        return getScaledThroughCache(avatarPlaceholderUrl, avatarPlaceholder, width, height);
    }

    @Override
    @Nullable
    public BufferedImage _get(@NotNull URL url) {
        return cache.get(url);
    }

    @Override
    @Nullable
    public BufferedImage _getScaled(@NotNull URL url, int width, int height) {
        BufferedImage nonScaled = cache.get(url);
        if (nonScaled == null) {
            try {
                nonScaled = ImageIO.read(url);
            } catch (IOException e) {
                LOGGER.warn("Failed to load image from " + url);
            }
        }
        return getScaledThroughCache(url, nonScaled, width, height);
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

    @Override
    public void _loadImages(@NotNull final Collection<URL> sources) {
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

    public IdeaImageUtil(WebUrlBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
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
                    URL remoteUrl = urlBuilder.assets(imageUri);
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

}
