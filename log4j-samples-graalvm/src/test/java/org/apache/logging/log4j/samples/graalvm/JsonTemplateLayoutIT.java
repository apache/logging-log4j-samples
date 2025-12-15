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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Integration test to run for a JSON Template Layout output.
 */
class JsonTemplateLayoutIT {

    static final String[] STANDARD_LEVELS = {"ERROR", "WARN", "INFO", "DEBUG"};

    @Test
    void verifyStdOut() {
        final Path basePath = Paths.get(System.getProperty("basedir"), "target", "logs");
        final Path logFile = basePath.resolve("out.log");

        assertThat(logFile).exists().isEmptyFile();
    }

    @Test
    void verifyLogFile() throws IOException {
        final Path basePath = Paths.get(System.getProperty("basedir"), "target", "logs");
        final Path logFile = basePath.resolve("file.log");

        assertThat(logFile).exists().isNotEmptyFile();

        try (final Stream<String> stream = Files.lines(logFile, StandardCharsets.UTF_8)) {
            String[] lines = stream.toArray(String[]::new);
            assertThat(lines).hasSize(STANDARD_LEVELS.length);
            for (int i = 0; i < STANDARD_LEVELS.length; i++) {
                String expectedLevel = STANDARD_LEVELS[i];
                assertThatJson(lines[i])
                        .as("Message nr %d", i + 1)
                        .whenIgnoringPaths("$['@timestamp']")
                        .isEqualTo(Map.of(
                                "ecs.version",
                                "1.2.0",
                                "log.level",
                                expectedLevel,
                                "message",
                                String.format("Hello %s!", expectedLevel),
                                "process.thread.name",
                                "main",
                                "log.logger",
                                Main.class.getName()));
            }
        }
    }
}
