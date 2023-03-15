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

package org.eclipse.hono.communication.api.config;

import java.util.List;

import org.eclipse.hono.util.CommandConstants;
import org.eclipse.hono.util.ConfigConstants;
import org.eclipse.hono.util.EventConstants;
import org.eclipse.hono.util.TelemetryConstants;

/**
 * Constant values for PubSub.
 */
public final class PubSubConstants {

    public static final String TENANT_NOTIFICATIONS = "registry-tenant.notification";
    public static final String EVENT_STATES_SUBTOPIC_ENDPOINT = "event.state";

    private PubSubConstants() {
    }

    /**
     * Gets the list of all topics need to be created per tenant.
     *
     * @return List of all topics.
     */
    public static List<String> getTopicsToCreate() {
        return List.of(EventConstants.EVENT_ENDPOINT,
                CommandConstants.COMMAND_ENDPOINT,
                CommandConstants.COMMAND_RESPONSE_ENDPOINT,
                ConfigConstants.CONFIG_RESPONSE_ENDPOINT,
                ConfigConstants.CONFIG_ENDPOINT,
                EVENT_STATES_SUBTOPIC_ENDPOINT,
                TelemetryConstants.TELEMETRY_ENDPOINT);
    }
}
