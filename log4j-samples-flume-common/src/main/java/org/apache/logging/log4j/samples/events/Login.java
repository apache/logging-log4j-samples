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
package org.apache.logging.log4j.samples.events;

import org.apache.logging.log4j.samples.dto.AuditEvent;

/**
 * Member logged in successfully.
 */

public interface Login extends AuditEvent {

    /**
     * Member : Member or End User number at the Host
     *
     * @param member Member or End User number at the Host
     */
    void setMember(String member);

    /**
     * Source : Source of the End User's request; or method user used to navigate (link, button)
     *
     * @param source Source of the End User's request; or method user used to navigate (link, button)
     */
    void setSource(String source);

    /**
     * Start Page Option : Chosen start page destination for IB login.
     *
     * @param startPageOption Chosen start page destination for IB login.
     */
    void setStartPageOption(String startPageOption);

}
