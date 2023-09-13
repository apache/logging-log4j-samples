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
package org.apache.logging.log4j.lookup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.status.StatusLogger;

/**
 *
 */

@Plugin(name = "custom", category = StrLookup.CATEGORY)
public class CustomLookup extends AbstractLookup {

    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final Marker LOOKUP = MarkerManager.getMarker("LOOKUP");

    private static ConcurrentMap<String, Map<String, String>> loggerProperties = new ConcurrentHashMap<>();

    /**
     * Looks up the value for the key using the data in the LogEvent.
     * @param event The current LogEvent.
     * @param key  the key to be looked up, may be null
     * @return The value associated with the key.
     */
    @Override
    public String lookup(final LogEvent event, final String key) {
        if (event == null) {
            return null;
        }
        try {
            final Map<String, String> properties = loggerProperties.get(event.getLoggerName());
            if (properties == null) {
                return "";
            }
            if (key == null || key.length() == 0 || key.equals("*")) {
                final StringBuilder sb = new StringBuilder("{");
                boolean first = true;
                for (final Map.Entry<String, String> entry : properties.entrySet()) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                    first = false;
                }
                sb.append("}");
                return sb.toString();
            }
            return properties.get(key);
        } catch (final Exception ex) {
            LOGGER.warn(LOOKUP, "Error while getting property [{}].", key, ex);
            return null;
        }
    }

    public static void setLoggerProperties(final String loggerName, final Map<String, String> properties) {
        loggerProperties.put(loggerName, properties);
    }

}
