package com.samebug.clients.matcher;

import com.samebug.clients.api.StackTraceListener;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class StackTraceMatcherTest {
    @Test
    public void testAndroidOutput() throws IOException {
        InputStream is = getClass().getResourceAsStream("/androidoutput.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));

        StackTraceListener listener = Mockito.mock(StackTraceListener.class);
        StackTraceMatcher matcher = new StackTraceMatcher(listener);

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                matcher.line(line);
            }
            matcher.end();

            Mockito.verify(listener).stacktraceFound(getTextResource("/stacktrace1.txt"));

            Mockito.verify(listener).stacktraceFound(getTextResource("/stacktrace2.txt"));

            Mockito.verify(listener).stacktraceFound(getTextResource("/stacktrace3.txt"));

            Mockito.verify(listener).stacktraceFound(getTextResource("/stacktrace4.txt"));

            Mockito.verify(listener).stacktraceFound(getTextResource("/stacktrace5.txt"));

            Mockito.verify(listener).stacktraceFound(getTextResource("/stacktrace6.txt"));

            Mockito.verify(listener).stacktraceFound(getTextResource("/stacktrace7.txt"));
        } finally {
            reader.close();
        }
    }

    @Test
    public void recognizeStandardFrame() {
        checkLineType(
                LineType.StackFrameType,
                "at com.android.server.job.JobSchedulerService$JobSchedulerStub.enforceValidJobRequest(JobSchedulerService.java:632)\n");
    }

    @Test
    public void recognizeFrameWithNumbers() {
        checkLineType(
                LineType.StackFrameType,
                "at android.app.ActivityThread.access$800(ActivityThread.java:143) \n");
    }

    @Test
    public void recognizeCauseWithoutMessage() {
        checkLineType(
                LineType.CausedByTypeWithoutMessage,
                "Caused by: java.lang.reflect.InvocationTargetException\n");
    }

    @Test
    public void recognizeCauseWithMessageStart() {
        checkLineType(
                LineType.CausedByTypeWithMessage,
                "Caused by: android.content.res.Resources$NotFoundException: Unable to find resource ID #0x0\n");
    }

    @Test
    public void recognizeExceptionStartWithMessage() {
        checkLineType(
                LineType.ExceptionStartTypeWithMessage,
                "android.view.InflateException: Binary XML file line #34: Error inflating class android.widget.FrameLayout\n");
    }

    @Test
    public void recognizeExceptionStartWithoutMessage() {
        checkLineType(
                LineType.ExceptionStartTypeWithoutMessage,
                "android.view.InflateException     \n");
    }

    private void checkLineType(LineType expectedLineType, String line) {
        StackTraceListener listener = Mockito.mock(StackTraceListener.class);
        StackTraceMatcher matcher = new StackTraceMatcher(listener);
        Line match = matcher.recognize(line);
        assertEquals(expectedLineType, match.getType());
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
        return b.toString();
    }
}