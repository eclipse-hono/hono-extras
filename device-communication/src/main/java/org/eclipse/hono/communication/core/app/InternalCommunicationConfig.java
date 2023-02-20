/*
 * ***********************************************************
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
 *  <p>
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *  <p>
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *  <p>
 *  SPDX-License-Identifier: EPL-2.0
 * **********************************************************
 *
 */

package org.eclipse.hono.communication.core.app;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Singleton;

/**
 * Configs for internal communication service
 */
@Singleton
public class InternalCommunicationConfig {

    @ConfigProperty(name = "app.projectId")
    String projectId;

    // Config
    @ConfigProperty(name = "app.internalCommunication.config.ackTopic")
    String configAckTopicFormat;
    @ConfigProperty(name = "app.internalCommunication.config.topicFormat")
    String configTopicFormat;

    // Command
    @ConfigProperty(name = "app.internalCommunication.command.topicFormat")
    String commandTopicFormat;

    public String getConfigTopicFormat() {
        return configTopicFormat;
    }

    public String getCommandTopicFormat() {
        return commandTopicFormat;
    }

    public String getConfigAckTopicFormat() {
        return configAckTopicFormat;
    }

    public String getProjectId() {
        return projectId;
    }
}
