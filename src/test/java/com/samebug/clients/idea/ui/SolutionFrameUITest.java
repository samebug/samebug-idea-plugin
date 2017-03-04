package com.samebug.clients.idea.ui;

import com.google.gson.*;
import com.samebug.clients.common.ui.component.solutions.ISolutionFrame;
import com.samebug.clients.swing.ui.global.ColorService;
import com.samebug.clients.swing.ui.global.FontService;
import com.samebug.clients.swing.ui.global.WebImageService;
import com.samebug.clients.swing.ui.global.IconService;
import com.samebug.clients.swing.ui.component.solutions.SolutionFrame;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class SolutionFrameUITest extends JDialog {
    private static final String resourceJson = "/com/samebug/clients/idea/ui/solutionFrame/t2w27.json";

    public SolutionFrameUITest(Gson gson) throws IOException {
        InputStream stream = getClass().getResourceAsStream(resourceJson);
        ISolutionFrame.Model model = gson.fromJson(new InputStreamReader(stream), SolutionFrame.Model.class);
        stream.close();

        SolutionFrame sf = new SolutionFrame(null);
        sf.loadingSucceeded(model);
        sf.showAuthenticationError();
        JComponent contentPane = sf;

        setContentPane(contentPane);
    }

    public static void main(String[] args) throws IOException, FontFormatException {
        FontService.registerFonts();
        ColorService.install(new TestColorService());
        WebImageService.install(new TestWebImageService());
        IconService.install(new TestIconService());

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