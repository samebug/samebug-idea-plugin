package com.samebug.clients.search.matcher;

import com.samebug.TestUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TeamCityDecoderTest {
    @Test
    @Ignore
    public void testWithoutSeparateMessage() throws IOException {
        String out = util.getTextResource("teamcity-0001-out.txt");
        String st1 = util.getTextResource("teamcity-0001-stacktrace-001.txt");
        assertTrue(TeamCityDecoder.isTestFrameworkException(out));
        assertThat(out, containsString(st1));
    }

    @Test
    @Ignore
    public void testWithSeparateMessageAndDetails() throws IOException {
        String out = util.getTextResource("teamcity-0002-out.txt");
        String st1 = util.getTextResource("teamcity-0002-stacktrace-001.txt");
        assertTrue(TeamCityDecoder.isTestFrameworkException(out));
        assertThat(out, containsString(st1));
    }

    private TestUtils util = new TestUtils(getClass());
}
