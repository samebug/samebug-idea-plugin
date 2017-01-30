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
package com.samebug.clients.idea.ui.controller.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.entities.RestHit;
import com.samebug.clients.common.search.api.entities.Tip;
import com.samebug.clients.common.search.api.exceptions.BadRequest;
import com.samebug.clients.idea.messages.client.TipModelListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import org.jetbrains.annotations.NotNull;

final class TipModelController implements TipModelListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final SearchTabController controller;
    final int mySearchId;

    public TipModelController(@NotNull final SearchTabController controller) {
        this.controller = controller;
        this.mySearchId = controller.mySearchId;

        MessageBusConnection projectConnection = controller.project.getMessageBus().connect(controller);
        projectConnection.subscribe(TipModelListener.TOPIC, this);
    }

    @Override
    public void startPostTip(int searchId) {
        if (searchId == mySearchId) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    controller.view.beginPostTip();
                }
            });
        }
    }

    @Override
    public void successPostTip(int searchId, RestHit<Tip> result) {
        if (searchId == mySearchId) {
            controller.service.addTip(result);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    controller.view.finishPostTipWithSuccess();
                    // TODO workaround to close
                    controller.view.tipPanel = null;
                    // TODO should we avoid refreshing the whole view?
                    controller.refreshTab();
                }
            });
        }
    }

    @Override
    public void failPostTip(int searchId, Exception e) {
        if (searchId == mySearchId) {
            final String errorMessageKey;
            if (e instanceof BadRequest) {
                final String writeTipErrorCode = ((BadRequest) e).getRestError().getCode();
                if ("UNRECOGNIZED_SOURCE".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.source.malformed";
                else if ("MESSAGE_TOO_SHORT".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.tip.short";
                else if ("MESSAGE_TOO_LONG".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.tip.long";
                else if ("NOT_YOUR_SEARCH".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.notYourSearch";
                else if ("NOT_EXCEPTION_SEARCH".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.notExceptionSearch";
                else if ("UNKNOWN_SEARCH".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.unknownSearch";
                else if ("UNREACHABLE_SOURCE".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.unreachableSource";
                else errorMessageKey = "samebug.tip.write.error.source.unhandledBadRequest";
            } else {
                errorMessageKey = "samebug.tip.write.error.source.unhandled";
            }
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    controller.view.finishPostTipWithError(SamebugBundle.message(errorMessageKey));
                }
            });
        }
    }

    @Override
    public void finishPostTip(int searchId) {
    }
}
