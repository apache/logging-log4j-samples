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
import org.junit.Assert;
import org.junit.Test;

public class StandardIT {

    @Test
    public void verifyStdOut() throws IOException {
        final Path basePath = Paths.get(System.getProperty("basedir"), "target", "logs");
        final Path logFile = basePath.resolve("out.log");

        Assert.assertTrue("Log file does not exist", Files.exists(logFile));
        Assert.assertTrue("Log file is empty", Files.size(logFile) > 0);

        final List<String> lines = Files.readAllLines(logFile, StandardCharsets.UTF_8);

        Assert.assertEquals(2, lines.size());
        Assert.assertTrue(lines.get(0)
                .matches("XML: .* INFO\\s+\\[main] o\\.a\\.l\\.l\\.s\\.j\\.Main Starting Log4j JLink Sample\\.\\.\\."));
        Assert.assertTrue(
                lines.get(1)
                        .matches(
                                "XML: .* WARN\\s+\\[main] o\\.a\\.l\\.l\\.s\\.j\\.Main Please add your name as command line parameter\\."));
    }
}
