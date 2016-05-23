package com.samebug.clients.search.matcher;

import com.samebug.clients.search.api.StackTraceListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class StackTraceMatcherTest {

    @Test
    public void testTabIndentedOutput() throws IOException {
        openReader("runIdea.txt");

        String line;
        while ((line = reader.readLine()) != null) {
            matcher.line(line);
        }
        matcher.end();

        Mockito.verify(listener).stacktraceFound(null, getTextResource("runIdea-stacktrace-001.txt"));
    }

    @Test
    public void testInvalidLongOutput() throws IOException {
        openReader("invalidLong.txt");

        String line;
        while ((line = reader.readLine()) != null) {
            matcher.line(line);
        }
        matcher.end();

        Mockito.verify(listener).stacktraceFound(null, getTextResource("invalidLong-stacktrace-001.txt"));
    }

    @Test
    public void testAndroidOutput() throws IOException {
        openReader("androidOutput.txt");

        String line;
        while ((line = reader.readLine()) != null) {
            matcher.line(line);
        }
        matcher.end();

        Mockito.verify(listener).stacktraceFound(null, getTextResource("androidOutput-stacktrace-001.txt"));
        Mockito.verify(listener).stacktraceFound(null, getTextResource("androidOutput-stacktrace-002.txt"));
        Mockito.verify(listener).stacktraceFound(null, getTextResource("androidOutput-stacktrace-003.txt"));
        Mockito.verify(listener).stacktraceFound(null, getTextResource("androidOutput-stacktrace-004.txt"));
        Mockito.verify(listener).stacktraceFound(null, getTextResource("androidOutput-stacktrace-005.txt"));
        Mockito.verify(listener).stacktraceFound(null, getTextResource("androidOutput-stacktrace-006.txt"));
        Mockito.verify(listener).stacktraceFound(null, getTextResource("androidOutput-stacktrace-007.txt"));
    }


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


    @Before
    public void initMatcher() {
        listener = Mockito.mock(StackTraceListener.class);
        matcher = new StackTraceMatcher(listener, null);
    }

    @After
    public void closeReader() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openReader(String resourceName) {
        InputStream is = getClass().getResourceAsStream(resourceName);
        reader = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
    }

    private BufferedReader reader;
    private StackTraceMatcher matcher;
    private StackTraceListener listener;
}