/**
 * Copyright 2016 Samebug, Inc.
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
import com.samebug.clients.search.api.SamebugClient;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;

/**
 * Created by poroszd on 2/25/16.
 */
public class ImageUtil {
    static final Hashtable<URL, Image> cache = new Hashtable<URL, Image>();
    static final Hashtable<ScaledKey, Image> scaledCache = new Hashtable<ScaledKey, Image>();
    static final Logger LOGGER = Logger.getInstance(ImageUtil.class);

    public static Image get(URL url) {
        return cache.get(url);
    }

    public static Image getScaled(URL url, int width, int height) {
        ScaledKey key = new ScaledKey(url, width, height);
        Image scaledImage = scaledCache.get(key);
        if (scaledImage == null) {
            Image nonScaledImage = cache.get(url);
            if (nonScaledImage == null) {
                return null;
            } else {
                scaledImage = nonScaledImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
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
                Image image = ImageIO.read(url);
                cache.put(url, image);
            } catch (IOException e) {
                LOGGER.warn("Failed to download image " + url);
            }
        }
    }


    static final String[] cachedImages = {
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
        for (String imageUri : cachedImages) {
            InputStream imageBytes = IdeaSamebugPlugin.class.getResourceAsStream("/com/samebug/cache/" + imageUri);
            if (imageBytes == null) {
                LOGGER.warn("Image " + imageUri + " was not found!");
            } else {
                try {
                    URL remoteUrl = samebugImageUrl(imageUri);
                    Image image = ImageIO.read(imageBytes);
                    cache.put(remoteUrl, image);
                } catch (MalformedURLException e) {
                    LOGGER.warn("Url of image " + imageUri + " could not be resolved!", e);
                } catch (IOException e) {
                    LOGGER.warn("Failed to read " + imageUri + " as an image!", e);
                }
            }
        }
    }

    static URL samebugImageUrl(String uri) throws MalformedURLException {
        return SamebugClient.root.resolve("assets/").resolve(uri).toURL();
    }

    static class ScaledKey {
        public URL src;
        public int height;
        public int width;

        public ScaledKey(URL src, int width, int height) {
            this.src = src;
            this.height = height;
            this.width = width;
        }
    }
}
