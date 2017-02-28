package com.samebug.clients.idea.ui;

import com.samebug.clients.swing.ui.ImageUtil;
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

final class TestImageUtil extends ImageUtil {
    private final Hashtable<URL, BufferedImage> cache = new Hashtable<URL, BufferedImage>();
    private final Hashtable<ScaledKey, BufferedImage> scaledCache = new Hashtable<ScaledKey, BufferedImage>();
    private final URL avatarPlaceholderUrl;
    private final BufferedImage avatarPlaceholder;

    @Override
    protected void _loadImages(@NotNull Collection<URL> sources) {

    }

    @Override
    protected BufferedImage _getScaled(@NotNull URL url, int width, int height) {
        return getScaledThroughCache(avatarPlaceholderUrl, avatarPlaceholder, width, height);
    }

    @Override
    protected BufferedImage _get(@NotNull URL url) {
        return cache.get(url);
    }

    @Override
    protected BufferedImage _getAvatarPlaceholder(int width, int height) {
        return getScaledThroughCache(avatarPlaceholderUrl, avatarPlaceholder, width, height);
    }

    @Override
    protected Image _getAvatarPlaceholder() {
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
            tmpAvatarUrl = ImageUtil.class.getResource("/com/samebug/cache/images/avatar-placeholder.png");
            if (tmpAvatarUrl != null) tmpAvatarImage = ImageIO.read(tmpAvatarUrl);
        } catch (IOException e) {
            System.err.println("Failed to read avatar-placeholder.png as an image!");
            e.printStackTrace();
        }
        avatarPlaceholderUrl = tmpAvatarUrl;
        avatarPlaceholder = tmpAvatarImage;

        for (String imageUri : cachedImages) {
            InputStream imageBytes = ImageUtil.class.getResourceAsStream("/com/samebug/cache/" + imageUri);
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
