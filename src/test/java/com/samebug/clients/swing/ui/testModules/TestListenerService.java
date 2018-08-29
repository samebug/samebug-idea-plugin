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
package com.samebug.clients.swing.ui.testModules;

import com.samebug.clients.swing.ui.modules.ListenerService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@SuppressWarnings("unchecked")
public final class TestListenerService extends ListenerService {
    @Override
    @NotNull
    protected <T> T internalGetListener(JComponent component, Class<T> listenerInterface) {
        T mapping = ListenerService.getListenerFromComponent(component, listenerInterface);
        if (mapping != null) {
            return mapping;
        } else {
            Class<?>[] interfaces = new Class<?>[]{listenerInterface};
            return (T) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    System.out.println(method);
                    return null;
                }
            });
        }
    }
}
