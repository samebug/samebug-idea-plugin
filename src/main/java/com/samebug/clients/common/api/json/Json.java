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
package com.samebug.clients.common.api.json;

import com.google.gson.*;
import com.samebug.clients.common.api.entities.search.Search;
import com.samebug.clients.common.api.entities.search.SearchGroup;
import com.samebug.clients.common.api.entities.solution.RestSolution;

import java.util.Date;

public class Json {
    public final static Gson gson;

    static {
        // TODO is this a fine way of serialization of Date?
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                }
        );
        gsonBuilder.registerTypeAdapter(RestSolution.class, new RestSolutionAdapter());
        gsonBuilder.registerTypeAdapter(Search.class, new SearchAdapter());
        gsonBuilder.registerTypeAdapter(SearchGroup.class, new SearchGroupAdapter());
        gson = gsonBuilder.create();
    }
}
