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
import org.apache.logging.log4j.util.PropertiesUtil;

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
    public static final String DEFAULT_PATTERN = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

    public CustomConfiguration(final LoggerContext loggerContext) {
        this(loggerContext, ConfigurationSource.NULL_SOURCE);
    }

    /**
     * Constructor to create the default configuration.
     */
    public CustomConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
        super(loggerContext, source);

        setName(CONFIG_NAME);
        final Layout<? extends Serializable> layout = PatternLayout.newBuilder()
                .withPattern(DEFAULT_PATTERN)
                .withConfiguration(this)
                .build();
        final Appender appender = ConsoleAppender.createDefaultAppenderForLayout(layout);
        appender.start();
        addAppender(appender);
        final LoggerConfig root = getRootLogger();
        root.addAppender(appender, null, null);

        final String levelName = PropertiesUtil.getProperties().getStringProperty(DEFAULT_LEVEL);
        final Level level = levelName != null && Level.valueOf(levelName) != null ?
                Level.valueOf(levelName) : Level.ERROR;
        root.setLevel(level);
    }

    @Override
    protected void doConfigure() {
    }
}
