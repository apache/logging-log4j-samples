/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.samples.graalvm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {

    static {
        try {
            Files.createDirectories(Paths.get("target", "logs"));
            java.util.logging.LogManager.getLogManager()
                    .readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Logger logger = LogManager.getLogger(Main.class);

    private Main() {}

    public static void main(final String[] args) {
        final List<Level> levels = new ArrayList<>(Arrays.asList(Level.values()));
        levels.remove(Level.OFF);
        levels.remove(Level.ALL);
        levels.remove(Level.FATAL);
        levels.stream().sorted().forEach(level -> logger.log(level, "Hello {}!", level));
    }
}
