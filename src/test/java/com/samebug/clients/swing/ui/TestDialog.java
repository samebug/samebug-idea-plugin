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
package com.samebug.clients.swing.ui;

import com.google.gson.*;
import com.samebug.clients.common.entities.user.SamebugUser;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.idea.ui.modules.IdeaMessageService;
import com.samebug.clients.swing.ui.modules.*;
import com.samebug.clients.swing.ui.testModules.TestColorService;
import com.samebug.clients.swing.ui.testModules.TestIconService;
import com.samebug.clients.swing.ui.testModules.TestListenerService;
import com.samebug.clients.swing.ui.testModules.TestTrackingService;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public abstract class TestDialog extends JDialog {
    private static final Gson gson;

    static {
        gson = createGson();

        try {
            installModules();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected TestDialog() {
        setPreferredSize(new Dimension(580, 600));
        setModal(true);
    }

    protected abstract void initializeUI(String resource) throws Exception;

    public TestDialog waitToInitializeUI(final String resource) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    try {
                        TestDialog.this.initializeUI(resource);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void showDialog() {
        pack();
        setVisible(true);
    }

    protected <T> T readJson(String resourceUrl, Class<T> classOfT) throws IOException {
        InputStream stream = getClass().getResourceAsStream(resourceUrl);
        try {
            return gson.fromJson(new InputStreamReader(stream), classOfT);
        } finally {
            stream.close();
        }
    }

    private static void installModules() throws Exception {
        FontService.registerFonts();
        ColorService.install(new TestColorService());
        WebImageService.install();
        IconService.install(new TestIconService());
        MessageService.install(new IdeaMessageService());
        ListenerService.install(new TestListenerService());
        TrackingService.install(new TestTrackingService());
    }

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return formatter.parseDateTime(json.getAsJsonPrimitive().getAsString()).toDate();
                    }
                }
        );
        gsonBuilder.registerTypeAdapter(SamebugUser.Base.class, new JsonDeserializer<SamebugUser.Base>() {
                    @Override
                    public SamebugUser.Base deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        if (json.getAsJsonObject().has("id")) {
                            return context.deserialize(json, SamebugUser.Registered.class);
                        } else {
                            return context.deserialize(json, SamebugUser.Visitor.class);
                        }
                    }
                }
        );
        return gsonBuilder.create();
    }
}
