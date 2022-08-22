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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.qpid.proton.message.Message;
import org.eclipse.hono.client.amqp.config.ClientConfigProperties;
import org.eclipse.hono.client.command.CommandConsumer;
import org.eclipse.hono.client.device.amqp.AmqpAdapterClient;
import org.eclipse.hono.client.device.amqp.CommandResponder;
import org.eclipse.hono.client.device.amqp.EventSender;
import org.eclipse.hono.client.device.amqp.TelemetrySender;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttEndpoint;

/**
 * Tracks MQTT connections per tenant and closes the AMQP connection automatically when the last MQTT connection of the
 * tenant is closed.
 * <p>
 * Note: {@link #connect(String, Vertx, ClientConfigProperties)} needs to be invoked before using the instance.
 */
public class MultiTenantConnectionManagerImpl implements MultiTenantConnectionManager {

    private final Map<String, TenantConnections> connectionsPerTenant = new HashMap<>();

    @Override
    public Future<Void> connect(final String tenantId, final Vertx vertx, final ClientConfigProperties clientConfig) {

        connectionsPerTenant.computeIfAbsent(tenantId, k -> {
            final TenantConnections tenantConnections = new TenantConnections(k, vertx, clientConfig);
            tenantConnections.connect();
            return tenantConnections;
        });

        return getTenantConnections(tenantId)
                .compose(tenantConnections -> tenantConnections.isConnected(clientConfig.getConnectTimeout()))
                .onFailure(ex -> {
                    final TenantConnections failedTenant = connectionsPerTenant.remove(tenantId);
                    if (failedTenant != null) {
                        failedTenant.closeAllConnections();
                    }
                });
    }

    @Override
    public Future<Void> addEndpoint(final String tenantId, final MqttEndpoint mqttEndpoint) {
        return getTenantConnections(tenantId)
                .compose(tenantConnections -> tenantConnections.addEndpoint(mqttEndpoint));
    }

    @Override
    public Future<Boolean> closeEndpoint(final String tenantId, final MqttEndpoint mqttEndpoint) {

        return getTenantConnections(tenantId)
                .map(tenantConnections -> tenantConnections.closeEndpoint(mqttEndpoint))
                .onSuccess(amqpLinkClosed -> {
                    if (amqpLinkClosed) {
                        connectionsPerTenant.remove(tenantId);
                    }
                });
    }

    @Override
    public void closeAllTenants() {
        connectionsPerTenant.forEach((k, connections) -> connections.closeAllConnections());
        connectionsPerTenant.clear();
    }

    @Override
    public Future<TelemetrySender> getOrCreateTelemetrySender(final String tenantId) {
        return getAmqpAdapterClient(tenantId).map(client -> client);
    }

    @Override
    public Future<EventSender> getOrCreateEventSender(final String tenantId) {
        return getAmqpAdapterClient(tenantId).map(client -> client);
    }

    @Override
    public Future<CommandResponder> getOrCreateCommandResponseSender(final String tenantId) {
        return getAmqpAdapterClient(tenantId).map(client -> client);
    }

    @Override
    public Future<CommandConsumer> createDeviceSpecificCommandConsumer(final String tenantId, final String deviceId,
            final Consumer<Message> messageHandler) {

        return getAmqpAdapterClient(tenantId)
                .compose(client -> client.createDeviceSpecificCommandConsumer(tenantId, deviceId, messageHandler));
    }

    @Override
    public Future<CommandConsumer> createCommandConsumer(final String tenantId,
            final Consumer<Message> messageHandler) {

        return getAmqpAdapterClient(tenantId).compose(client -> client.createCommandConsumer(messageHandler));
    }

    private Future<TenantConnections> getTenantConnections(final String tenantId) {
        final TenantConnections tenantConnections = connectionsPerTenant.get(tenantId);
        if (tenantConnections == null) {
            return Future.failedFuture("tenant [" + tenantId + "] is not connected");
        } else {
            return Future.succeededFuture(tenantConnections);
        }
    }

    private Future<AmqpAdapterClient> getAmqpAdapterClient(final String tenantId) {
        return getTenantConnections(tenantId).compose(TenantConnections::getAmqpAdapterClient);
    }
}
