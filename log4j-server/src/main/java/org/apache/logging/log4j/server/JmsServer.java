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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LogEventListener;
import org.apache.logging.log4j.jms.appender.JmsAppender;
import org.apache.logging.log4j.jms.appender.JmsManager;
import org.apache.logging.log4j.jndi.JndiManager;

/**
 * LogEventListener server that receives LogEvents over a JMS {@link javax.jms.Destination}.
 *
 * @since 2.1
 */
public class JmsServer extends LogEventListener implements MessageListener, LifeCycle {

    private final AtomicReference<State> state = new AtomicReference<>(State.INITIALIZED);
    private final JmsManager jmsManager;
    private MessageConsumer messageConsumer;

    public JmsServer(
            final String connectionFactoryBindingName,
            final String connectionFactoryName,
            final String providerURL,
            final String destinationBindingName,
            final String username,
            final char[] password,
            final Properties jndiProperties) {
        final String managerName = JmsServer.class.getName() + '@' + JmsServer.class.hashCode();
        final Properties jndiManager = JndiManager.createProperties(
                connectionFactoryBindingName, providerURL, null, null, null, jndiProperties);
        jmsManager = JmsManager.getJmsManager(
                managerName,
                jndiManager,
                connectionFactoryName,
                destinationBindingName,
                username,
                password,
                false,
                JmsAppender.Builder.DEFAULT_RECONNECT_INTERVAL_MILLIS);
    }

    @Override
    public State getState() {
        return state.get();
    }

    @Override
    public void onMessage(final Message message) {
        try {
            if (message instanceof ObjectMessage) {
                final Object body = ((ObjectMessage) message).getObject();
                if (body instanceof LogEvent) {
                    log((LogEvent) body);
                } else {
                    LOGGER.warn("Expected ObjectMessage to contain LogEvent. Got type {} instead.", body.getClass());
                }
            } else {
                LOGGER.warn(
                        "Received message of type {} and JMSType {} which cannot be handled.",
                        message.getClass(),
                        message.getJMSType());
            }
        } catch (final JMSException e) {
            LOGGER.catching(e);
        }
    }

    @Override
    public void initialize() {}

    @Override
    public void start() {
        if (state.compareAndSet(State.INITIALIZED, State.STARTING)) {
            try {
                messageConsumer = jmsManager.createMessageConsumer();
                messageConsumer.setMessageListener(this);
            } catch (final JMSException e) {
                throw new LoggingException(e);
            }
        }
    }

    @Override
    public void stop() {
        stop(AbstractLifeCycle.DEFAULT_STOP_TIMEOUT, AbstractLifeCycle.DEFAULT_STOP_TIMEUNIT);
    }

    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        boolean stopped = true;
        try {
            messageConsumer.close();
        } catch (final JMSException e) {
            LOGGER.debug("Exception closing {}", messageConsumer, e);
            stopped = false;
        }
        return stopped && jmsManager.stop(timeout, timeUnit);
    }

    @Override
    public boolean isStarted() {
        return state.get() == State.STARTED;
    }

    @Override
    public boolean isStopped() {
        return state.get() == State.STOPPED;
    }

    /**
     * Starts and runs this server until the user types "exit" into standard input.
     *
     * @throws IOException
     */
    public void commandLineLoop() throws IOException {
        System.out.println("Type \"exit\" to quit.");
        final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
        while (true) {
            final String line = stdin.readLine();
            if (line == null || line.equalsIgnoreCase("exit")) {
                System.out.println("Exiting. Kill the application if it does not exit due to daemon threads.");
                this.stop();
                return;
            }
        }
    }
}
