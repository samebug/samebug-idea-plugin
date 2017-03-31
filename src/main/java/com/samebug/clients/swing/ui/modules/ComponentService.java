package com.samebug.clients.swing.ui.modules;

import java.awt.*;

public final class ComponentService {
    public static <T> T findAncestor(Component component, Class<T> searchedClass) {
        for (Component c = component; c != null; c = c.getParent()) {
            if (searchedClass.isInstance(c)) return searchedClass.cast(c);
        }
        return null;
    }

}
