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
package com.samebug.clients.common.ui.modules;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TextService {
    static final PrettyTime pretty = new PrettyTime(Locale.US);
    // IMPROVE can't we use java 8 api?
    static final DateFormat recent = new SimpleDateFormat("HH:mm");
    static final DateFormat older = new SimpleDateFormat("yyyy.MM.dd");


    public static String prettyTime(final Date date) {
        return pretty.format(date);
    }

    public static String lineSeparator = System.getProperty("line.separator");

    /**
     * Uses HH:mm format for dates in the last 12 hours, or yyyy.MM.dd for older dates.
     */
    public static String adaptiveTimestamp(Date date) {
        long now = System.currentTimeMillis();
        long then = date.getTime();
        if (Math.abs(now - then) >= 1000 * 60 * 60 * 12) return olderTimestamp(date);
        else return recentTimestamp(date);
    }

    public static String recentTimestamp(Date date) {
        return recent.format(date);
    }

    public static String olderTimestamp(Date date) {
        return older.format(date);
    }

    private TextService() {}
}
