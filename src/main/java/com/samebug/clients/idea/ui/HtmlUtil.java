package com.samebug.clients.idea.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.search.api.SamebugClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by poroszd on 2/25/16.
 */
public class HtmlUtil {
    public static final ImageCache imageCache = new ImageCache();

    private final static Logger LOGGER = Logger.getInstance(HtmlUtil.class);
    private static final String[] cachedImages = {
            "images/sources/github.png",
            "images/sources/googlegroups.png",
            "images/sources/jenkins.png",
            "images/sources/mod-mbox.png",
            "images/sources/page.png",
            "images/sources/samebug.png",
            "images/sources/samebug-justbug.png",
            "images/sources/sourceforge.png",
            "images/sources/stackoverflow.png",
            "images/sources/web.png",
            "images/sources/youtrack.png"
    };

    static {
        for (String imageUri : cachedImages) {
            InputStream imageBytes = IdeaSamebugPlugin.class.getResourceAsStream("/com/samebug/cache/" + imageUri);
            if (imageBytes == null) {
                LOGGER.warn("Image " + imageUri + " was not found!");
            } else {
                try {
                    URL remoteUrl = samebugImageUrl(imageUri);
                    Image image = ImageIO.read(imageBytes);
                    imageCache.put(remoteUrl, image);
                } catch (MalformedURLException e) {
                    LOGGER.warn("Url of image " + imageUri + " could not be resolved!", e);
                } catch (IOException e) {
                    LOGGER.warn("Failed to read " + imageUri + " as an image!", e);
                }
            }
        }
    }

    private static URL samebugImageUrl(String uri) throws MalformedURLException {
        return SamebugClient.root.resolve("assets/").resolve(uri).toURL();
    }

}
