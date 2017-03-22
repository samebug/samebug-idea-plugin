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
package com.samebug.clients.idea.ui.controller.authentication;

import com.samebug.clients.common.api.entities.profile.LoggedInUser;
import com.samebug.clients.common.api.form.AnonymousUse;
import com.samebug.clients.common.ui.component.authentication.IAnonymousUseForm;
import com.samebug.clients.idea.ui.controller.form.AnonymousUseFormHandler;

public final class AnonymousUseListener implements IAnonymousUseForm.Listener {
    final AuthenticationController controller;

    public AnonymousUseListener(AuthenticationController controller) {
        this.controller = controller;
    }

    @Override
    public void useAnonymously(final IAnonymousUseForm source) {
        new AnonymousUseFormHandler(controller.view, source, new AnonymousUse()) {
            @Override
            protected void afterPostForm(LoggedInUser response) {
                source.successPost();
                controller.twc.focusOnHelpRequestList();
            }
        }.execute();
    }
}