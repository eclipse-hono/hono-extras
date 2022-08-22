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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.hono.client.ServerErrorException;
import org.eclipse.hono.client.amqp.config.ClientConfigProperties;
import org.eclipse.hono.client.amqp.connection.ConnectionLifecycle;
import org.eclipse.hono.client.amqp.connection.HonoConnection;
import org.eclipse.hono.client.device.amqp.AmqpAdapterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttEndpoint;

/**
 * Manages all connections of one tenant, MQTT connections of devices as well as the AMQP connection to Hono's AMQP
 * adapter.
 * <p>
 * By invoking {@link #connect()} an AMQP client for the tenant is connected. Each MQTT endpoint needs to be added to
 * keep track of all MQTT connections belonging to the tenant. When the last MQTT endpoint for the tenant is closed, the
 * AMQP client - and thus this instance - is closed automatically.
 * <p>
 * Note: do not re-use an instance if it is already closed.
 */
class TenantConnections {

    // visible for testing
    final List<MqttEndpoint> mqttEndpoints = new ArrayList<>();

    private final AmqpAdapterClient amqpAdapterClient;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String tenantId;

    private boolean closed = false;

    /**
     * Creates a new instance with a new {@link AmqpAdapterClient} and a new {@link HonoConnection}.
     *
     * @param tenantId The ID of the tenant whose connections are to be managed
     * @param vertx The Vert.x instance to be used by the HonoConnection.
     * @param clientConfig The client configuration to be used by the HonoConnection.
     */
    TenantConnections(final String tenantId, final Vertx vertx, final ClientConfigProperties clientConfig) {
        this(AmqpAdapterClient.create(HonoConnection.newConnection(vertx, clientConfig)), tenantId);
    }

    /**
     * Creates a new instance for the given {@link AmqpAdapterClient}.
     * <p>
     * <b>This constructor is for testing purposes only.</b>
     *
     * @param amqpAdapterClient The AmqpAdapterClient to use.
     * @param tenantId The ID of the tenant whose connections are to be managed
     */
    TenantConnections(final AmqpAdapterClient amqpAdapterClient, final String tenantId) {
        this.amqpAdapterClient = amqpAdapterClient;
        this.tenantId = tenantId;
    }

    /**
     * Opens a connection to Hono's AMQP protocol adapter for the tenant to be managed.
     *
     * @return A future indicating the outcome of the operation.
     */
    public Future<HonoConnection> connect() {
        return getAmqpAdapterClient().compose(ConnectionLifecycle::connect)
                .onSuccess(con -> log.debug("Connected to AMQP adapter"));
    }

    /**
     * Adds an MQTT endpoint for the tenant.
     *
     * @param mqttEndpoint The endpoint to add.
     * @return A future indicating the outcome of the operation.
     */
    public Future<Void> addEndpoint(final MqttEndpoint mqttEndpoint) {
        return failIfClosed()
                .onFailure(thr -> log.warn("failed to add MQTT endpoint for tenant [{}]", tenantId, thr))
                .onSuccess(v -> mqttEndpoints.add(mqttEndpoint));
    }

    /**
     * Closes the given MQTT endpoint and if there are no other MQTT endpoints present, it closes the AMQP client and
     * this instance.
     *
     * @param mqttEndpoint The endpoint to be closed.
     * @return {@code true} if the AMQP connection has been closed.
     */
    public boolean closeEndpoint(final MqttEndpoint mqttEndpoint) {

        closeEndpointIfConnected(mqttEndpoint);

        mqttEndpoints.remove(mqttEndpoint);

        if (mqttEndpoints.isEmpty()) {
            closeThisInstance();
        }

        return closed;
    }

    /**
     * Closes all MQTT endpoints and the AMQP connection.
     */
    public void closeAllConnections() {
        log.info("closing all AMQP connections");

        mqttEndpoints.forEach(this::closeEndpointIfConnected);
        mqttEndpoints.clear();
        closeThisInstance();
    }

    private void closeEndpointIfConnected(final MqttEndpoint mqttEndpoint) {
        if (mqttEndpoint.isConnected()) {
            log.debug("closing connection with client [tenant ID: {}, client ID: {}]", tenantId,
                    mqttEndpoint.clientIdentifier());
            mqttEndpoint.close();
        } else {
            log.trace("connection to client is already closed");
        }
    }

    private void closeThisInstance() {
        amqpAdapterClient.disconnect();
        closed = true;
    }

    /**
     * Checks whether the AMQP connection is currently established.
     *
     * @param connectTimeout The maximum number of milliseconds to wait for an ongoing connection attempt to finish.
     * @return A succeeded future if this connection is established. Otherwise, the future will be failed with a
     *         {@link ServerErrorException}, or an {@link IllegalStateException} if this instance is already closed.
     */
    public Future<Void> isConnected(final long connectTimeout) {
        return getAmqpAdapterClient().compose(f -> f.isConnected(connectTimeout));
    }

    /**
     * Returns the AmqpAdapterClient for the tenant.
     *
     * @return A future containing the AmqpAdapterClient, or, if this instance is already closed, a failed future.
     */
    public Future<AmqpAdapterClient> getAmqpAdapterClient() {
        return failIfClosed()
                .onFailure(thr -> log.warn("failed to get client for tenant [{}]", tenantId, thr))
                .map(amqpAdapterClient);
    }

    private Future<Void> failIfClosed() {
        if (closed) {
            return Future.failedFuture(new IllegalStateException("connections for this tenant are already closed"));
        }
        return Future.succeededFuture();
    }
}
