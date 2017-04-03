/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.search;

import com.intellij.execution.testframework.sm.runner.states.TestErrorState;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import org.apache.commons.lang.ArrayUtils;

import java.text.ParseException;
import java.util.Map;

/**
 * Tests write to the console do this in teamcity's format. See https://confluence.jetbrains.com/display/TCD10/Build+Script+Interaction+with+TeamCity
 * This means that we have to decode it to find stack traces from test runners.
 * <p>
 * However, converting the teamcity failed state to its actual presentation text is not trivially available, so we kind of hack it.
 */
public class TeamCityDecoder {
    public static boolean isTestFrameworkException(String line) {
        return line.startsWith("##teamcity[testFailed ");
    }

    public static String[] testFailureLines(String line) {
        try {
            ServiceMessage msg = ServiceMessage.parse(line.trim());
            if (msg != null) {
                Map<String, String> attributes = msg.getAttributes();
                if (attributes.containsKey("message") && attributes.containsKey("details")) {
                    String state = TestErrorState.buildErrorPresentationText(attributes.get("message"), attributes.get("details"));
                    if (state != null) {
                        return state.split("\n");
                    } else {
                        return ArrayUtils.EMPTY_STRING_ARRAY;
                    }
                } else {
                    return ArrayUtils.EMPTY_STRING_ARRAY;
                }
            } else {
                return ArrayUtils.EMPTY_STRING_ARRAY;
            }
        } catch (ParseException e) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
    }
}
