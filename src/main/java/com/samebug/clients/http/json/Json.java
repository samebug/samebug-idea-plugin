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
package com.samebug.clients.http.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samebug.clients.http.entities.notification.Notification;
import com.samebug.clients.http.entities.search.HitScore;
import com.samebug.clients.http.entities.search.QueryInfo;
import com.samebug.clients.http.entities.solution.Document;
import com.samebug.clients.http.entities.user.SamebugUser;
import com.samebug.clients.http.entities.user.User;

public final class Json {
    public static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        gsonBuilder.registerTypeAdapter(QueryInfo.class, new QueryInfoAdapter());
        gsonBuilder.registerTypeAdapter(User.class, new UserAdapter());
        gsonBuilder.registerTypeAdapter(SamebugUser.class, new SamebugUserAdapter());
        gsonBuilder.registerTypeAdapter(Document.class, new DocumentAdapter());
        gsonBuilder.registerTypeAdapter(HitScore.class, new HitScoreAdapter());
        gsonBuilder.registerTypeAdapter(Notification.class, new NotificationAdapter());
        gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
        gson = gsonBuilder.create();
    }

    private Json() {}
}
