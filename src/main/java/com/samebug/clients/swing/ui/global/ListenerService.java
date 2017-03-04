package com.samebug.clients.swing.ui.global;

import javax.swing.*;

public abstract class ListenerService {
    private static ListenerService INSTANCE = null;

    public static void install(ListenerService instance) {
        assert INSTANCE == null : "ListenerService has already been installed";
        INSTANCE = instance;
    }

    public static <T> T getListener(JComponent component, Class<T> listenerClass) {
        return INSTANCE.internalGetListener(component, listenerClass);
    }

    protected abstract <T> T internalGetListener(JComponent component, Class<T> listenerClass);
}
