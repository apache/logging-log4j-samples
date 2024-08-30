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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Integration test to run for the SimpleLogger implementation.
 */
public class SimpleIT {

    static final String[] STANDARD_LEVELS = {"ERROR", "WARN", "INFO", "DEBUG", "TRACE"};

    @Test
    void verifyStdOut() throws IOException {
        final Path basePath = Paths.get(System.getProperty("basedir"), "target", "logs");
        final Path logFile = basePath.resolve("out.log");

        assertThat(logFile).exists().isNotEmptyFile();

        final List<String> expected = Stream.of(STANDARD_LEVELS)
                .map(l -> String.format("%s %s Hello %s!", l, Main.class.getName(), l))
                .toList();

        try (final Stream<String> stream = Files.lines(logFile, StandardCharsets.UTF_8)) {
            assertThat(stream).containsExactlyElementsOf(expected);
        }
    }
}
