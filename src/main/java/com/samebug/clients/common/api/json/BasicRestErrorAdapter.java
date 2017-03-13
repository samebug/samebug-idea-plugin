package com.samebug.clients.common.api.json;

import com.google.gson.*;
import com.samebug.clients.common.api.client.FormRestError;
import com.samebug.clients.common.api.client.BasicRestError;
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
