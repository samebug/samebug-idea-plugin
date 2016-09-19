package com.samebug.clients.search.matcher;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TeamCityDecoderTest {
    @Test
    @Ignore
    public void testWithoutSeparateMessage() throws IOException {
        String out = getTextResource("teamcity-0001-out.txt");
        String st1 = getTextResource("teamcity-0001-stacktrace-001.txt");
        assertTrue(TeamCityDecoder.isTestFrameworkException(out));
        assertThat(out, containsString(st1));
    }

    @Test
    @Ignore
    public void testWithSeparateMessageAndDetails() throws IOException {
        String out = getTextResource("teamcity-0002-out.txt");
        String st1 = getTextResource("teamcity-0002-stacktrace-001.txt");
        assertTrue(TeamCityDecoder.isTestFrameworkException(out));
        assertThat(out, containsString(st1));
    }

    // TODO extract resource reading
    private String getTextResource(String path) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        StringBuilder b = new StringBuilder();
        String line;
        Boolean first = true;
        while ((line = reader.readLine()) != null) {
            if (!first) b.append("\n");
            else first = false;
            b.append(line);
        }
        reader.close();
        return b.toString();
    }
}
