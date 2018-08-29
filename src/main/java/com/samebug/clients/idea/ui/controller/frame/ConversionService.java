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
package com.samebug.clients.idea.ui.controller.frame;

import com.samebug.clients.common.ui.component.profile.ConnectionStatus;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.welcome.IWelcomeFrame;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.search.QueryInfo;
import com.samebug.clients.http.entities.search.StackTraceInfo;
import com.samebug.clients.http.entities.user.Me;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConversionService {
    public ConversionService() {
    }

    public IProfilePanel.Model profilePanel(Me user, UserStats statistics) {
        ConnectionStatus status = IdeaSamebugPlugin.getInstance().clientService.getWsClient().isConnected() ? ConnectionStatus.ONLINE : ConnectionStatus.OFFLINE;
        // TODO @poroszd change profile panel!
        return new IProfilePanel.Model(0, statistics.getNumberOfVotes(), statistics.getNumberOfTips(), statistics.getNumberOfThanks(),
                user.getDisplayName(), user.getAvatarUrl(), status);
    }

    public IWelcomeFrame.Model convertWelcomeFrame(Me user, UserStats statistics) {
        IProfilePanel.Model profile = profilePanel(user, statistics);

        return new IWelcomeFrame.Model(profile);
    }

    public static String headLine(QueryInfo search) {
        if (search instanceof StackTraceInfo) {
            StackTraceInfo i = (StackTraceInfo) search;
            return headLine(i.getExceptionType(), i.getExceptionMessage());
        } else {
            throw new IllegalArgumentException(search + " is not stack trace");
        }
    }

    public static String headLine(@NotNull String typeName, @Nullable String message) {
        return (message != null) ? typeName + ": " + message : typeName;
    }
}
