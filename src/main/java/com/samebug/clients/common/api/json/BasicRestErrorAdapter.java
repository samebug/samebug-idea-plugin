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
import com.samebug.clients.common.api.client.BasicRestError;
import com.samebug.clients.common.api.client.FormRestError;
import com.samebug.clients.common.api.client.RestError;

import java.lang.reflect.Type;

final class BasicRestErrorAdapter implements JsonDeserializer<BasicRestError> {
    @Override
    public BasicRestError deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) return null;
        else {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement typeFieldValue = jsonObject.get("formErrors");
            if (typeFieldValue == null) return context.deserialize(jsonObject, RestError.class);
            else return context.deserialize(jsonObject, FormRestError.class);
        }
    }
}
