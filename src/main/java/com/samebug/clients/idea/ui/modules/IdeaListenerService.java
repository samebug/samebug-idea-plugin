/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.idea.ui.modules;

import com.samebug.clients.swing.ui.modules.ListenerService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public final class IdeaListenerService extends ListenerService {
    @Override
    @NotNull
    protected <T> T internalGetListener(JComponent component, Class<T> listenerInterface) {
        T mapping = ListenerService.getListenerFromComponent(component, listenerInterface);
        if (mapping == null) {
            // this means a programming error, the controller has no binding for this interface.
            throw new RuntimeException("No listener bound in component " + component + " for interface " + listenerInterface);
        } else {
            return mapping;
        }
    }
}
