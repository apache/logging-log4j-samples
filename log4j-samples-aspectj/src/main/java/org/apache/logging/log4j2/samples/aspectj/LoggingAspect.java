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
package org.apache.logging.log4j2.samples.aspectj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.SourceLocation;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final FlowMessageFactory FLOW_MESSAGE_FACTORY = new DefaultFlowMessageFactory();

    public LoggingAspect() {}

    private static StackTraceElement getLocation(final JoinPoint joinPoint) {
        final SourceLocation location = joinPoint.getSourceLocation();
        final String methodName = joinPoint.getSignature().getName();
        String fileName = null;
        try {
            fileName = location.getFileName();
        } catch (final Exception e) {
            LOGGER.debug("Failed to get the file name from the source location.", e);
        }
        int lineNumber = -1;
        try {
            lineNumber = location.getLine();
        } catch (final Exception e) {
            LOGGER.debug("Failed to get the line number from the source location.", e);
        }
        return new StackTraceElement(location.getWithinType().getName(), methodName, fileName, lineNumber);
    }

    @Pointcut("execution(public * org.apache.logging..*(..))")
    public void traceLogging() {}

    @Before("traceLogging()")
    public void traceEntry(final JoinPoint jp) {
        LOGGER.atTrace()
                .withMarker(AbstractLogger.ENTRY_MARKER)
                .withLocation(getLocation(jp))
                .log(FLOW_MESSAGE_FACTORY.newEntryMessage(null, jp.getArgs()));
    }

    @AfterReturning(pointcut = "traceLogging()", returning = "response")
    public void logMethodResponse(final JoinPoint jp, final Object response) {
        LOGGER.atTrace()
                .withMarker(AbstractLogger.EXIT_MARKER)
                .withLocation(getLocation(jp))
                .log(FLOW_MESSAGE_FACTORY.newExitMessage(null, response));
    }

    @AfterThrowing(pointcut = "traceLogging()", throwing = "exception")
    public void logMethodException(final JoinPoint jp, final Throwable exception) {
        LOGGER.atError()
                .withMarker(AbstractLogger.THROWING_MARKER)
                .withLocation(getLocation(jp))
                .log("Throwing", exception);
    }
}
