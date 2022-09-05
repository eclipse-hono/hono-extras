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

package org.eclipse.hono.gateway.sdk.mqtt2amqp;

import java.util.function.Consumer;

import org.apache.qpid.proton.message.Message;
import org.eclipse.hono.client.amqp.config.ClientConfigProperties;
import org.eclipse.hono.client.command.CommandConsumer;
import org.eclipse.hono.client.device.amqp.CommandResponder;
import org.eclipse.hono.client.device.amqp.EventSender;
import org.eclipse.hono.client.device.amqp.TelemetrySender;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttEndpoint;

/**
 * Manages connections for multiple tenants.
 * <p>
 * <b>NB</b> The {@link #connect(String, Vertx, ClientConfigProperties)} method needs to be invoked before calling any
 * of the other methods.
 */
public interface MultiTenantConnectionManager {

    /**
     * Connects to Hono's AMQP adapter with the given configuration.
     *
     * @param tenantId The tenant to connect.
     * @param vertx The Vert.x instance to use for the connection.
     * @param clientConfig The configuration of the connection.
     * @return a succeeded future if the connection could be established within the time frame configured with
     *         {@link ClientConfigProperties#getConnectTimeout()}, a failed future otherwise.
     */
    Future<Void> connect(String tenantId, Vertx vertx, ClientConfigProperties clientConfig);

    /**
     * Adds an MQTT endpoint for the given tenant.
     *
     * @param tenantId The tenant to which the endpoint belongs.
     * @param mqttEndpoint The endpoint to be added.
     * @return A future indicating the outcome of the operation. The future will succeed if the endpoint has been added
     *         successfully. Otherwise the future will fail with a failure message indicating the cause of the failure.
     */
    Future<Void> addEndpoint(String tenantId, MqttEndpoint mqttEndpoint);

    /**
     * Closes the given MQTT endpoint and if there are no other open endpoints for this tenant, it closes the
     * corresponding AMQP connection.
     *
     * @param tenantId The tenant to which the endpoint belongs.
     * @param mqttEndpoint The endpoint to be closed.
     * @return A future indicating the outcome of the operation. The future will succeed with a boolean that is
     *         {@code true} if the AMQP connection (and all MQTT endpoints) have been closed. If an error occurs, the
     *         future will fail with a failure message indicating the cause of the failure.
     */
    Future<Boolean> closeEndpoint(String tenantId, MqttEndpoint mqttEndpoint);

    /**
     * Closes all connections, MQTT connections as well as AMQP connections for all tenants.
     */
    void closeAllTenants();

    /**
     * Gets a client for sending telemetry data to Hono's AMQP protocol adapter.
     *
     * @param tenantId The tenant to which the sender belongs.
     * @return a future with the open sender or a failed future.
     */
    Future<TelemetrySender> getOrCreateTelemetrySender(String tenantId);

    /**
     * Gets a client for sending events to Hono's AMQP protocol adapter.
     *
     * @param tenantId The tenant to which the sender belongs.
     * @return a future with the open sender or a failed future.
     */
    Future<EventSender> getOrCreateEventSender(String tenantId);

    /**
     * Gets a client for sending command responses to Hono's AMQP protocol adapter.
     *
     * @param tenantId The tenant to which the sender belongs.
     * @return a future with the open sender or a failed future.
     */
    Future<CommandResponder> getOrCreateCommandResponseSender(String tenantId);

    /**
     * Creates a client for consuming commands from Hono's AMQP protocol adapter for a specific device.
     *
     * @param tenantId The tenant to which the sender belongs.
     * @param deviceId The device to consume commands for.
     * @param messageHandler The handler to invoke with every command received.
     * @return a future with the open sender or a failed future.
     */
    Future<CommandConsumer> createDeviceSpecificCommandConsumer(String tenantId, String deviceId,
            Consumer<Message> messageHandler);

    /**
     * Creates a client for consuming commands from Hono's AMQP protocol adapter for all devices of this tenant.
     *
     * @param tenantId The tenant to which the sender belongs.
     * @param messageHandler The handler to invoke with every command received.
     * @return a future with the open sender or a failed future.
     */
    Future<CommandConsumer> createCommandConsumer(String tenantId, Consumer<Message> messageHandler);

}
