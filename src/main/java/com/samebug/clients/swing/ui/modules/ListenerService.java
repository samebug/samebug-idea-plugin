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
package com.samebug.clients.swing.ui.modules;

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
