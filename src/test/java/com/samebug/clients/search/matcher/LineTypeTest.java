package com.samebug.clients.search.matcher;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LineTypeTest {
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

    @Test
    public void recognizeMoreLine() {
        checkLineType(
                LineType.MoreType,
                "... 7 more\n");
    }

    @Test
    public void recognizeMoreLineWithTab() {
        checkLineType(
                LineType.MoreType,
                "\t... 7 more\n");
    }

    private void checkLineType(LineType expectedLineType, String line) {
        Line match = StackTraceMatcher.recognize(line);
        assertEquals(expectedLineType, match.getType());
    }
}
