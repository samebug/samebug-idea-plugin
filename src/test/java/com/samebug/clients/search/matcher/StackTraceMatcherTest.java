package com.samebug.clients.search.matcher;

import com.samebug.TestUtils;
import com.samebug.clients.search.api.StackTraceListener;
import com.samebug.clients.search.api.entities.tracking.DebugSessionInfo;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class StackTraceMatcherTest {

    @Test
    public void testTabIndentedOutput() throws IOException {
        matcher.append(util.getTextResource("runIdea.txt"));
        matcher.end();

        assertThat(listener.foundStackTraces.get(0), containsString(util.getTextResource("runIdea-stacktrace-001.txt")));
    }

    @Test
    public void testInvalidLongOutput() throws IOException {
        matcher.append(util.getTextResource("invalidLong.txt"));
        matcher.end();

        assertThat(listener.foundStackTraces.get(0), containsString(util.getTextResource("invalidLong-stacktrace-001.txt")));
    }

    @Test
    public void testAndroidOutput() throws IOException {
        matcher.append(util.getTextResource("androidOutput.txt"));
        matcher.end();

        assertThat(listener.foundStackTraces.get(0), containsString(util.getTextResource("androidOutput-stacktrace-001.txt")));
        assertThat(listener.foundStackTraces.get(1), containsString(util.getTextResource("androidOutput-stacktrace-002.txt")));
        assertThat(listener.foundStackTraces.get(2), containsString(util.getTextResource("androidOutput-stacktrace-003.txt")));
        assertThat(listener.foundStackTraces.get(3), containsString(util.getTextResource("androidOutput-stacktrace-004.txt")));
        assertThat(listener.foundStackTraces.get(4), containsString(util.getTextResource("androidOutput-stacktrace-005.txt")));
        assertThat(listener.foundStackTraces.get(5), containsString(util.getTextResource("androidOutput-stacktrace-006.txt")));
        assertThat(listener.foundStackTraces.get(6), containsString(util.getTextResource("androidOutput-stacktrace-007.txt")));
    }

    @Test
    public void testOutputForTwoImmediateFollowingStackTraces() throws IOException {
        matcher.append(util.getTextResource("0001-out.txt"));
        matcher.end();

        assertThat(listener.foundStackTraces.get(0), containsString(util.getTextResource("0001-stacktrace-001.txt")));
        assertThat(listener.foundStackTraces.get(1), containsString(util.getTextResource("0001-stacktrace-002.txt")));
    }


    @Before
    public void initMatcher() {
        listener = new StackTraceBuffer();
        matcher = new StackTraceMatcher(listener, null);
    }

    private StackTraceMatcher matcher;
    private StackTraceBuffer listener;
    private TestUtils util = new TestUtils(getClass());

    private class StackTraceBuffer implements StackTraceListener {
        final ArrayList<String> foundStackTraces = new ArrayList<String>();

        @Override
        public void stacktraceFound(@Nullable DebugSessionInfo sessionInfo, String stacktrace) {
            foundStackTraces.add(stacktrace);
        }
    }
}