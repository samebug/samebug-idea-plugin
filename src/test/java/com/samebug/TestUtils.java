/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class TestUtils {
    private final Class<?> clazz;

    public TestUtils(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getTextResource(String path) throws IOException {
        InputStream is = clazz.getResourceAsStream(path);
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
}
