/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.modules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class ListenerService {
    private static ListenerService INSTANCE = null;

    public static void install(ListenerService instance) {
        assert INSTANCE == null : "ListenerService has already been installed";
        INSTANCE = instance;
    }

    @NotNull
    public static <T> T getListener(JComponent component, Class<T> listenerInterface) {
        return INSTANCE.internalGetListener(component, listenerInterface);
    }

    @NotNull
    protected abstract <T> T internalGetListener(JComponent component, Class<T> listenerInterface);

    @Nullable
    protected static <T> T getListenerFromComponent(JComponent component, Class<T> listenerInterface) {
        DataService.Key<T> key = createKey(listenerInterface);
        return DataService.getData(component, key);
    }

    public static <T> void putListenerToComponent(JComponent component, Class<T> bindInterface, T listener) {
        DataService.Key<T> key = createKey(bindInterface);

        // We want to notice if the component already has a listener.
        // If it becomes inconvenient to not be able to bind more objects to the same listener interface, we can simply use a list in the DataService.Key .
        assert DataService.getData(component, key) == null : "Component " + component + " already has a listener for " + bindInterface;

        DataService.putData(component, key, listener);
    }

    private static <T> DataService.Key<T> createKey(Class<T> listenerClass) {
        return new DataService.Key<T>(listenerClass.getCanonicalName());
    }
}

