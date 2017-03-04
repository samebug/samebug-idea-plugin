package com.samebug.clients.swing.ui.global;

import javax.swing.*;
import java.awt.*;

public final class DataService {
    public static final Key<Integer> SolutionId = new Key<Integer>("SolutionId");

    @SuppressWarnings("unchecked")
    public static <T> T getData(JComponent component, Key<T> key) {
        for (Component c = component; c != null; c = c.getParent()) {
            if (!(c instanceof JComponent)) continue;
            Object data = ((JComponent) c).getClientProperty(key.name);
            if (data != null) return (T) data;
        }
        return null;
    }

    public static <T> void putData(JComponent component, Key<T> key, T data) {
        component.putClientProperty(key.name, data);
    }

    public static final class Key<T> {
        private final String name;

        public Key(String name) {
            this.name = "samebug." + name;
        }
    }
}
