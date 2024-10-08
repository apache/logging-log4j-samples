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
package org.apache.logging.log4j.samples.android;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.test.appender.ListAppender;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private static Configuration configuration;
    private static ListAppender appender;
    private static Logger logger;

    @BeforeClass
    public static void setup() {
        LoggerContext context = LoggerContext.getContext(false);
        configuration = context.getConfiguration();
        appender = configuration.getAppender("LIST");
        logger = context.getLogger(MainActivity.class);
    }

    @Test
    public void hasExpectedConfiguration() {
        assertThat(configuration.getConfigurationSource().getLocation())
                .startsWith("jar:file:")
                .endsWith("!/log4j2-test.xml");
        // Check appenders
        Appender console = configuration.getAppender("CONSOLE");
        assertThat(console).as("Console Appender").isInstanceOf(ConsoleAppender.class);
        Appender list = configuration.getAppender("LIST");
        assertThat(list).as("List Appender").isInstanceOf(ListAppender.class);
        // Check logger configurations
        assertThat(configuration.getLoggers()).hasSize(2);
        assertThat(configuration.getRootLogger().getExplicitLevel()).isEqualTo(Level.INFO);
        assertThat(configuration.getRootLogger().getAppenders())
                .hasSize(2)
                .containsExactly(entry("CONSOLE", console), entry("LIST", list));
        assertThat(configuration.getLoggerConfig(MainActivity.class.getName()))
                .isNotNull()
                .extracting(LoggerConfig::getExplicitLevel)
                .isEqualTo(Level.DEBUG);
    }

    @Test
    public void logMessagesAreDelivered() {
        assertThat(logger.getLevel()).isEqualTo(Level.DEBUG);
        appender.clear();
        for (int buttonId : MainActivity.buttonIds) {
            onView(withId(buttonId)).perform(click());
        }
        Level[] expectedLevels = {Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG};
        String[] expectedMessages =
                Arrays.stream(expectedLevels).map(l -> l + "-Hello " + l + "!").toArray(String[]::new);
        assertThat(appender.getMessages()).hasSize(expectedLevels.length).containsExactly(expectedMessages);
    }

    @Test
    public void logLevelChanges() {
        assertThat(logger.getLevel()).isEqualTo(Level.DEBUG);
        onView(withId(R.id.setLogLevelBtn)).perform(click());
        onView(withText("Set log level")).check(matches(isDisplayed()));
        onView(withText("ERROR")).perform(click());
        onView(withText("Select")).perform(click());
        onView(withText("Set log level")).check(doesNotExist());
        assertThat(logger.getLevel()).isEqualTo(Level.ERROR);
    }

    private static Map.Entry<String, Appender> entry(String name, Appender appender) {
        return new AbstractMap.SimpleImmutableEntry<>(name, appender);
    }
}