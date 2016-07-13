/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.search.api.client;

import com.intellij.util.net.HttpConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * @author Eugene Zhuravlev
 *         Date: 12-Jul-16
 */
final class ProxyCredentialsFacade {
    @NotNull
    private final HttpConfigurable myDelegate;
    @Nullable
    private final Field myLoginField;

    public ProxyCredentialsFacade(@NotNull HttpConfigurable delegate) {
        myDelegate = delegate;
        myLoginField = findLoginField();
    }


    public String getLogin() {
        if (myLoginField != null) {
            try {
                return (String) myLoginField.get(myDelegate);
            } catch (IllegalAccessException e) {
                // should not happen, as the field was public
                throw new RuntimeException(e);
            }
        }
        return myDelegate.getProxyLogin();
    }

    public String getPassword() {
        return myDelegate.getPlainProxyPassword();
    }

    @Nullable
    private static Field findLoginField() {
        try {
            return HttpConfigurable.class.getDeclaredField("PROXY_LOGIN"); // absent in the new version
        } catch (NoSuchFieldException ignored) {
        }
        return null;
    }
}
