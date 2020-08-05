/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import org.eclipse.hono.client.HonoConnection;
import org.eclipse.hono.client.device.amqp.AmqpAdapterClientFactory;
import org.eclipse.hono.config.ClientConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttEndpoint;

/**
 * Manages all connections of one tenant, MQTT connections of devices as well as the AMQP connection to Hono's AMQP
 * adapter.
 * <p>
 * Note: do not re-use an instance if it is already closed.
 */
class TenantConnections {

    // visible for testing
    final List<MqttEndpoint> mqttEndpoints = new ArrayList<>();

    private final AmqpAdapterClientFactory amqpAdapterClientFactory;
    private final Logger log = LoggerFactory.getLogger(getClass());

    private boolean closed = false;

    TenantConnections(final String tenantId, final Vertx vertx, final ClientConfigProperties clientConfig) {
        this(AmqpAdapterClientFactory.create(HonoConnection.newConnection(vertx, clientConfig), tenantId));
    }

    TenantConnections(final AmqpAdapterClientFactory amqpAdapterClientFactory) {
        this.amqpAdapterClientFactory = amqpAdapterClientFactory;
    }

    public TenantConnections connect() {
        getAmqpAdapterClientFactory().connect().onSuccess(con -> log.debug("Connected to AMQP adapter"));
        return this;
    }

    public void addEndpoint(final MqttEndpoint mqttEndpoint) {
        checkNotClosed();
        mqttEndpoints.add(mqttEndpoint);
    }

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
            log.debug("closing connection with client [client ID: {}]", mqttEndpoint.clientIdentifier());
            mqttEndpoint.close();
        } else {
            log.trace("connection to client is already closed");
        }
    }

    private void closeThisInstance() {
        getAmqpAdapterClientFactory().disconnect();
        closed = true;
    }

    public Future<Void> isConnected(final long connectTimeout) {
        return getAmqpAdapterClientFactory().isConnected(connectTimeout);
    }

    public AmqpAdapterClientFactory getAmqpAdapterClientFactory() throws IllegalStateException {
        checkNotClosed();
        return amqpAdapterClientFactory;
    }

    private void checkNotClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("all connections for this tenant are already closed");
        }
    }
}
