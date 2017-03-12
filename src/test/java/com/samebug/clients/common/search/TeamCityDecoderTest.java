package com.samebug.clients.common.search;

import com.samebug.TestUtils;
import com.samebug.clients.common.search.TeamCityDecoder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class TeamCityDecoderTest {
    @Test
    @Ignore("Cannot find if results like these still exists in the latest version. It has |n|r as line separators, and details contains the message of the stacktrace")
    public void testWithoutSeparateMessage() throws IOException {
        String out = util.getTextResource("teamcity-0001-out.txt");
        String st1 = util.getTextResource("teamcity-0001-stacktrace-001.txt");

        assertTrue(TeamCityDecoder.isTestFrameworkException(out));

        String[] decodedLines = TeamCityDecoder.testFailureLines(out);
        assertArrayEquals(decodedLines, st1.split("\n"));
    }

    @Test
    public void testWithSeparateMessageAndDetails() throws IOException {
        String out = util.getTextResource("teamcity-0002-out.txt");
        String st1 = util.getTextResource("teamcity-0002-stacktrace-001.txt");

        assertTrue(TeamCityDecoder.isTestFrameworkException(out));

        String[] decodedLines = TeamCityDecoder.testFailureLines(out);
        assertArrayEquals(decodedLines, st1.split("\n"));
    }

    @Test
    public void testWithMutlipleLines() throws IOException {
        String out = util.getTextResource("teamcity-0003-out.txt");
        String st1 = util.getTextResource("teamcity-0003-stacktrace-001.txt");
        for (String line : out.split("\n")) {
            if (TeamCityDecoder.isTestFrameworkException(out)) {
                assertThat(out, containsString(st1));
            }
        }
    }

    private TestUtils util = new TestUtils(getClass());
}
