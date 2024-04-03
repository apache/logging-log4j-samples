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
package org.apache.logging.log4j.samples.configuration;

import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * This Configuration is the same as the DefaultConfiguration but shows how a custom configuration can be built
 * programmatically
 */
public class CustomConfiguration extends AbstractConfiguration {

    private static final long serialVersionUID = 1L;

    /**
     * The name of the default configuration.
     */
    public static final String CONFIG_NAME = "Custom";

    /**
     * The System Property used to specify the logging level.
     */
    public static final String DEFAULT_LEVEL = "org.apache.logging.log4j.level";
    /**
     * The default Pattern used for the default Layout.
     */
    public static final String DEFAULT_PATTERN = "Custom: %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

    public CustomConfiguration(final LoggerContext loggerContext) {
        this(loggerContext, ConfigurationSource.NULL_SOURCE);
    }

    /**
     * Constructor to create the default configuration.
     */
    public CustomConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
        super(loggerContext, source);
        setName(CONFIG_NAME);
    }

    @Override
    protected void doConfigure() {
        // 1. Create appenders
        final Layout<? extends Serializable> layout = PatternLayout.newBuilder()
                .withConfiguration(this)
                .withPattern(DEFAULT_PATTERN)
                .build();
        final Appender appender = ConsoleAppender.newBuilder()
                .setConfiguration(this)
                .setName("Console")
                .setLayout(layout)
                .build();
        addAppender(appender);
        final LoggerConfig root = getRootLogger();
        // 2. Configure loggers
        final LoggerConfig logger = LoggerConfig.newBuilder()
                .withLoggerName("MyCustomLogger")
                .withConfig(this)
                .withLevel(Level.DEBUG)
                .build();
        addLogger(logger.getName(), logger);
        // 3. Configure root logger
        root.addAppender(appender, null, null);
        root.setLevel(Level.INFO);
    }
}
