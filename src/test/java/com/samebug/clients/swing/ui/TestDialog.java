package com.samebug.clients.swing.ui;

import com.google.gson.*;
import com.samebug.clients.idea.ui.modules.IdeaMessageService;
import com.samebug.clients.swing.ui.modules.*;
import com.samebug.clients.swing.ui.testModules.TestColorService;
import com.samebug.clients.swing.ui.testModules.TestIconService;
import com.samebug.clients.swing.ui.testModules.TestListenerService;
import com.samebug.clients.swing.ui.testModules.TestWebImageService;
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
        WebImageService.install(new TestWebImageService());
        IconService.install(new TestIconService());
        MessageService.install(new IdeaMessageService());
        ListenerService.install(new TestListenerService());
    }

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
                        return formatter.parseDateTime(json.getAsJsonPrimitive().getAsString()).toDate();
                    }
                }
        );
        return gsonBuilder.create();
    }
}
