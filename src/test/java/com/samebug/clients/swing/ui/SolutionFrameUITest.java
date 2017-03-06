package com.samebug.clients.swing.ui;

import com.google.gson.*;
import com.samebug.clients.common.ui.component.solutions.ISolutionFrame;
import com.samebug.clients.idea.ui.global.IdeaMessageService;
import com.samebug.clients.swing.ui.component.solutions.SolutionFrame;
import com.samebug.clients.swing.ui.global.*;
import com.samebug.clients.swing.ui.globalService.TestColorService;
import com.samebug.clients.swing.ui.globalService.TestIconService;
import com.samebug.clients.swing.ui.globalService.TestListenerService;
import com.samebug.clients.swing.ui.globalService.TestWebImageService;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class SolutionFrameUITest extends JDialog {
    private static final String resourceJson = "/com/samebug/clients/idea/ui/solutionFrame/t2w27.json";

    public SolutionFrameUITest(Gson gson) throws IOException, InvocationTargetException, InterruptedException {
        InputStream stream = getClass().getResourceAsStream(resourceJson);
        final ISolutionFrame.Model model = gson.fromJson(new InputStreamReader(stream), SolutionFrame.Model.class);
        stream.close();

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                SolutionFrame sf = new SolutionFrame();
                sf.loadingSucceeded(model);
                setContentPane(sf);
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


        SolutionFrameUITest dialog = new SolutionFrameUITest(gson);

        dialog.setPreferredSize(new Dimension(580, 600));
        dialog.setMinimumSize(new Dimension(200, 400));
        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}