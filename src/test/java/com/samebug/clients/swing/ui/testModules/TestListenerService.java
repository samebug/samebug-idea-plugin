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
package com.samebug.clients.swing.ui.testModules;

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.api.form.FormBuilder;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.swing.ui.modules.ListenerService;

import javax.swing.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;

@SuppressWarnings("unchecked")
public final class TestListenerService extends ListenerService {
    @Override
    protected <T> T internalGetListener(JComponent component, Class<T> listenerClass) {
        if (listenerClass == IHelpOthersCTA.Listener.class) {
            return (T) new IHelpOthersCTA.Listener() {
                @Override
                public void postTip(IHelpOthersCTA source, String tipBody) {
                    try {
                        source.failPostTipWithFormError(Collections.singletonList(new FieldError(FormBuilder.CreateTip.BODY, FormBuilder.CreateTip.E_TOO_LONG)));
                    } catch (FormMismatchException e) {
                        e.printStackTrace();
                    }
                }
            };
        } else if (listenerClass == IMarkButton.Listener.class) {
            return (T) new IMarkButton.Listener() {
                @Override
                public void markClicked(IMarkButton markButton, Integer solutionId, Integer currentMarkId) {
                    markButton.setLoading();
                }
            };
        } else {
            Class<?>[] interfaces = new Class<?>[]{listenerClass};
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
