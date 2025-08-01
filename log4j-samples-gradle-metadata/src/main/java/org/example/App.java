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
package org.example;

public class App {
    org.apache.logging.log4j.message.ParameterizedMessage m; // needs errorprone
    org.apache.logging.log4j.status.StatusData d; // needs spotbugs
    org.apache.logging.log4j.util.SystemPropertiesPropertySource s; // needs aQute.bnd
    org.apache.logging.log4j.util.Activator a; // needs osgi
    org.apache.logging.log4j.spi.LoggerRegistry<?> r; // needs jspecify

    public static void main(String[] args) {
        System.out.println("Hello from: " + App.class.getName());
    }
}
