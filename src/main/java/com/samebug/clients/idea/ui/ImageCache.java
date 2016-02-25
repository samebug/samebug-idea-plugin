package com.samebug.clients.idea.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by poroszd on 2/25/16.
 */
public class ImageCache extends Dictionary<URL, Image> {
    public static final Hashtable<URL, Image> cache = new Hashtable<URL, Image>();

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public Enumeration<URL> keys() {
        return cache.keys();
    }

    @Override
    public Enumeration<Image> elements() {
        return cache.elements();
    }

    @Override
    public Image get(Object key) {
        if (!(key instanceof URL)) return null;
        else {
            URL src = (URL) key;
            Image image = cache.get(src);
            if (image == null) {
                // mimic the behaviour of default image loading, as seen at
                // http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/7u40-b43/javax/swing/text/html/ImageView.java#690
                image = Toolkit.getDefaultToolkit().createImage(src);
                if (image != null) {
                    // Force the image to be loaded by using an ImageIcon.
                    ImageIcon ii = new ImageIcon();
                    ii.setImage(image);
                }
            }
            return image;
        }

    }

    @Override
    public Image put(URL key, Image value) {
        return cache.put(key, value);
    }

    @Override
    public Image remove(Object key) {
        return cache.remove(key);
    }
}
