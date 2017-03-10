package com.samebug.clients.swing.ui;

import com.google.gson.*;
import com.samebug.clients.common.ui.frame.tipRequestList.ITipRequestListFrame;
import com.samebug.clients.idea.ui.modules.IdeaMessageService;
import com.samebug.clients.swing.ui.frame.tipRequestList.TipRequestListFrame;
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
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

// TODO extract common ui test code
public class TipRequestListFrameUITest extends JDialog {
    private static final String resourceJson = "/com/samebug/clients/idea/ui/frame/tipRequestList/2.json";

    public TipRequestListFrameUITest(Gson gson) throws IOException, InvocationTargetException, InterruptedException {
        InputStream stream = getClass().getResourceAsStream(resourceJson);
        final ITipRequestListFrame.Model model = gson.fromJson(new InputStreamReader(stream), ITipRequestListFrame.Model.class);
        stream.close();

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                TipRequestListFrame f = new TipRequestListFrame();
                f.loadingSucceeded(model);
                setContentPane(f);
            }
        });


    }

    public static void main(String[] args) throws IOException, FontFormatException, InvocationTargetException, InterruptedException {
        FontService.registerFonts();
        ColorService.install(new TestColorService());
        WebImageService.install(new TestWebImageService());
        IconService.install(new TestIconService());
        MessageService.install(new IdeaMessageService());
        ListenerService.install(new TestListenerService());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
                        return formatter.parseDateTime(json.getAsJsonPrimitive().getAsString()).toDate();
                    }
                }
        );
        Gson gson = gsonBuilder.create();


        TipRequestListFrameUITest dialog = new TipRequestListFrameUITest(gson);

        dialog.setPreferredSize(new Dimension(580, 600));
        dialog.setMinimumSize(new Dimension(200, 400));
        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}