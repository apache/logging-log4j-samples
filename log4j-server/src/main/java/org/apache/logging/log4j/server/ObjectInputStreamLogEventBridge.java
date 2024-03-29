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
package org.apache.logging.log4j.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LogEventListener;
import org.apache.logging.log4j.util.FilteredObjectInputStream;

/**
 * Reads and logs serialized {@link LogEvent} objects (created with {@link SerializedLayout}) from an {@link ObjectInputStream}.
 *
 * @deprecated Java Serialization has inherent security weaknesses, see https://www.owasp.org/index.php/Deserialization_of_untrusted_data .
 * Therefore {@link SerializedLayout} is deprecated, and so is this class. We recommend using {@link JsonInputStreamLogEventBridge} instead.
 */
@Deprecated
public class ObjectInputStreamLogEventBridge extends AbstractLogEventBridge<ObjectInputStream> {

    private final List<String> allowedClasses;

    public ObjectInputStreamLogEventBridge() {
        this(Collections.<String>emptyList());
    }

    /**
     * Constructs an ObjectInputStreamLogEventBridge with additional allowed classes to deserialize.
     *
     * @param allowedClasses class names to also allow for deserialization
     * @since 2.8.2
     */
    public ObjectInputStreamLogEventBridge(final List<String> allowedClasses) {
        this.allowedClasses = allowedClasses;
    }

    @Override
    public void logEvents(final ObjectInputStream inputStream, final LogEventListener logEventListener)
            throws IOException {
        try {
            logEventListener.log((LogEvent) inputStream.readObject());
        } catch (final ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ObjectInputStream wrapStream(final InputStream inputStream) throws IOException {
        return new FilteredObjectInputStream(inputStream, allowedClasses);
    }
}
