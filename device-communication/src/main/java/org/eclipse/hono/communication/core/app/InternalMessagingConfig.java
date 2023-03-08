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

import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Configs for internal communication service.
 */
@Singleton
public class InternalMessagingConfig {

    @ConfigProperty(name = "app.projectId")
    String projectId;

    // Message Attributes
    @ConfigProperty(name = "app.internalMessaging.message.attributeKeys.deviceIdKey")
    String deviceIdKey;
    @ConfigProperty(name = "app.internalMessaging.message.attributeKeys.tenantIdKey")
    String tenantIdKey;
    @ConfigProperty(name = "app.internalMessaging.message.attributeKeys.configVersionIdKey")
    String configVersionIdKey;
    @ConfigProperty(name = "app.internalMessaging.message.attributeKeys.contentTypeKey")
    String contentTypeKey;

    //Event
    @ConfigProperty(name = "app.internalMessaging.event.topicFormat")
    String eventTopicFormat;

    @ConfigProperty(name = "app.internalMessaging.event.emptyNotificationEventContentType")
    String emptyNotificationEventContentType;

    // State
    @ConfigProperty(name = "app.internalMessaging.state.topicFormat")
    String stateTopicFormat;

    // Config
    @ConfigProperty(name = "app.internalMessaging.config.ackTopic")
    String configAckTopicFormat;
    @ConfigProperty(name = "app.internalMessaging.config.topicFormat")
    String configTopicFormat;

    // Command
    @ConfigProperty(name = "app.internalMessaging.command.topicFormat")
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

    public String getContentTypeKey() {
        return contentTypeKey;
    }

    public String getEventTopicFormat() {
        return eventTopicFormat;
    }

    public String getDeviceIdKey() {
        return deviceIdKey;
    }

    public String getTenantIdKey() {
        return tenantIdKey;
    }

    public String getConfigVersionIdKey() {
        return configVersionIdKey;
    }

    public String getEmptyNotificationEventContentType() {
        return emptyNotificationEventContentType;
    }

    public String getStateTopicFormat() {
        return stateTopicFormat;
    }
}
