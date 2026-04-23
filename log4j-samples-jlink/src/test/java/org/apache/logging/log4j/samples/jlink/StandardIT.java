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
package org.apache.logging.log4j.samples.jlink;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StandardIT {

    @Test
    public void verifyStdOut() throws IOException {
        final Path basePath = Paths.get(System.getProperty("basedir"), "target", "logs");
        final Path logFile = basePath.resolve("out.log");

        Assertions.assertTrue(Files.exists(logFile), "Log file does not exist");
        Assertions.assertTrue(Files.size(logFile) > 0, "Log file is empty");

        final List<String> lines = Files.readAllLines(logFile, StandardCharsets.UTF_8);

        Assertions.assertEquals(2, lines.size());
        Assertions.assertTrue(lines.get(0)
                .matches("XML: .* INFO\\s+\\[main] o\\.a\\.l\\.l\\.s\\.j\\.Main Starting Log4j JLink Sample\\.\\.\\."));
        Assertions.assertTrue(
                lines.get(1)
                        .matches(
                                "XML: .* WARN\\s+\\[main] o\\.a\\.l\\.l\\.s\\.j\\.Main Please add your name as command line parameter\\."));
    }
}
