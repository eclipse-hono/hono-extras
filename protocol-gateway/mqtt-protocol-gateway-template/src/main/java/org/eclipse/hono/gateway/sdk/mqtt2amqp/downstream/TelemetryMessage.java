/*******************************************************************************
 * Copyright (c) 2020, 2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.hono.gateway.sdk.mqtt2amqp.downstream;

import java.util.Objects;

import org.eclipse.hono.util.QoS;

import io.vertx.core.buffer.Buffer;

/**
 * This class holds required data of a telemetry message.
 */
public final class TelemetryMessage extends DownstreamMessage {

    private final QoS qos;

    /**
     * Creates an instance.
     *
     * @param payload The payload to be used.
     * @param qos The quality of service level.
     * @throws NullPointerException if payload or qos is {@code null}.
     */
    public TelemetryMessage(final Buffer payload, final QoS qos) {
        super(Objects.requireNonNull(payload));
        this.qos = Objects.requireNonNull(qos);
    }

    /**
     * Gets the quality of service level to use for sending the message.
     *
     * @return The quality of service level.
     */
    public QoS getQos() {
        return qos;
    }
}
