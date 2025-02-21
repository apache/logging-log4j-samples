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
package org.apache.logging.log4j.samples.nashorn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.test.appender.ListAppender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;

class ScriptFilterTest {

    @Test
    @Timeout(value = 10, threadMode = ThreadMode.SEPARATE_THREAD)
    void workingScriptFilter() throws Exception {
        URI configLocation = Objects.requireNonNull(ScriptFilterTest.class.getResource("/ScriptFilterTest.xml"))
                .toURI();
        LoggerContext context =
                (LoggerContext) LogManager.getContext(ScriptFilterTest.class.getClassLoader(), false, configLocation);
        // Get logger and appender
        ListAppender appender = context.getConfiguration().getAppender("LIST");
        assertNotNull(appender);
        appender.clear();
        Logger logger = context.getLogger(ScriptFilterTest.class);
        // Log some events
        logger.info("No marker");
        logger.info(MarkerManager.getMarker("AUDIT"), "AUDIT");
        logger.info(MarkerManager.getMarker("DENY"), "DENY");
        assertThat(appender.getMessages()).containsExactly("No marker", "AUDIT");
    }
}
