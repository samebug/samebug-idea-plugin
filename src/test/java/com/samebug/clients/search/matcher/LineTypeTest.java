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
                LineType.CausedByType,
                "Caused by: java.lang.reflect.InvocationTargetException\n");
    }

    @Test
    public void recognizeCauseWithMessageStart() {
        checkLineType(
                LineType.CausedByType,
                "Caused by: android.content.res.Resources$NotFoundException: Unable to find resource ID #0x0\n");
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
        LineType lineType = LineType.match(line);
        assertEquals(expectedLineType, lineType);
    }
}
