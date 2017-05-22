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
import java.awt.*;

public final class DataService {
    public static final Key<Integer> SolutionId = new Key<Integer>("SolutionId");
    public static final Key<Integer> SolutionHitIndex = new Key<Integer>("SolutionHitIndex");


    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getData(JComponent component, Key<T> key) {
        for (Component c = component; c != null; c = c.getParent()) {
            if (!(c instanceof JComponent)) continue;
            Object data = ((JComponent) c).getClientProperty(key.name);
            if (data != null) return (T) data;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getComponentData(@NotNull JComponent component, @NotNull Key<T> key) {
        Object data = component.getClientProperty(key.name);
        if (data != null) return (T) data;
        else return null;
    }

    public static <T> void putData(@NotNull JComponent component, @NotNull Key<T> key, @Nullable T data) {
        component.putClientProperty(key.name, data);
    }

    public static final class Key<T> {
        private final String name;

        public Key(String name) {
            this.name = "samebug." + name;
        }
    }

    private DataService() {}
}
