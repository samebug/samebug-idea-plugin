package com.samebug.clients.search.matcher;

import com.samebug.clients.search.api.StackTraceListener;
import com.samebug.clients.search.api.entities.tracking.DebugSessionInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class StackTraceMatcherTest {

    @Test
    public void testTabIndentedOutput() throws IOException {
        openReader("runIdea.txt");

        String line;
        while ((line = reader.readLine()) != null) {
            matcher.line(line);
        }
        matcher.end();

        assertThat(listener.foundStackTraces.get(0), containsString(getTextResource("runIdea-stacktrace-001.txt")));
    }

    @Test
    public void testInvalidLongOutput() throws IOException {
        openReader("invalidLong.txt");

        String line;
        while ((line = reader.readLine()) != null) {
            matcher.line(line);
        }
        matcher.end();

        assertThat(listener.foundStackTraces.get(0), containsString(getTextResource("invalidLong-stacktrace-001.txt")));
    }

    @Test
    public void testAndroidOutput() throws IOException {
        openReader("androidOutput.txt");

        String line;
        while ((line = reader.readLine()) != null) {
            matcher.line(line);
        }
        matcher.end();

        assertThat(listener.foundStackTraces.get(0), containsString(getTextResource("androidOutput-stacktrace-001.txt")));
        assertThat(listener.foundStackTraces.get(1), containsString(getTextResource("androidOutput-stacktrace-002.txt")));
        assertThat(listener.foundStackTraces.get(2), containsString(getTextResource("androidOutput-stacktrace-003.txt")));
        assertThat(listener.foundStackTraces.get(3), containsString(getTextResource("androidOutput-stacktrace-004.txt")));
        assertThat(listener.foundStackTraces.get(4), containsString(getTextResource("androidOutput-stacktrace-005.txt")));
        assertThat(listener.foundStackTraces.get(5), containsString(getTextResource("androidOutput-stacktrace-006.txt")));
        assertThat(listener.foundStackTraces.get(6), containsString(getTextResource("androidOutput-stacktrace-007.txt")));
    }

    @Test
    public void testOutputForTwoImmediateFollowingStackTraces() throws IOException {
        openReader("0001-out.txt");

        String line;
        while ((line = reader.readLine()) != null) {
            matcher.line(line);
        }
        matcher.end();

        assertThat(listener.foundStackTraces.get(0), containsString(getTextResource("0001-stacktrace-001.txt")));
        assertThat(listener.foundStackTraces.get(1), containsString(getTextResource("0001-stacktrace-002.txt")));
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
        listener = new StackTraceBuffer();
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
    private StackTraceBuffer listener;

    private class StackTraceBuffer implements StackTraceListener {
        final ArrayList<String> foundStackTraces = new ArrayList<String>();

        @Override
        public void stacktraceFound(@Nullable DebugSessionInfo sessionInfo, String stacktrace) {
            foundStackTraces.add(stacktrace);
        }
    }
}