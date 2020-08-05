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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.qpid.proton.message.Message;
import org.eclipse.hono.client.MessageConsumer;
import org.eclipse.hono.client.device.amqp.AmqpAdapterClientFactory;
import org.eclipse.hono.client.device.amqp.CommandResponder;
import org.eclipse.hono.client.device.amqp.EventSender;
import org.eclipse.hono.client.device.amqp.TelemetrySender;
import org.eclipse.hono.config.ClientConfigProperties;

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

        connectionsPerTenant.computeIfAbsent(tenantId, k -> new TenantConnections(k, vertx, clientConfig).connect());

        return getTenantConnections(tenantId)
                .isConnected(clientConfig.getConnectTimeout())
                .onFailure(ex -> {
                    final TenantConnections failedTenant = connectionsPerTenant.remove(tenantId);
                    if (failedTenant != null) {
                        failedTenant.closeAllConnections();
                    }
                });
    }

    @Override
    public void addEndpoint(final String tenantId, final MqttEndpoint mqttEndpoint) {
        getTenantConnections(tenantId).addEndpoint(mqttEndpoint);
    }

    @Override
    public boolean closeEndpoint(final String tenantId, final MqttEndpoint mqttEndpoint) {

        final boolean amqpLinkClosed = getTenantConnections(tenantId).closeEndpoint(mqttEndpoint);
        if (amqpLinkClosed) {
            connectionsPerTenant.remove(tenantId);
        }

        return amqpLinkClosed;
    }

    @Override
    public void closeAllTenants() {
        connectionsPerTenant.forEach((k, connections) -> connections.closeAllConnections());
        connectionsPerTenant.clear();
    }

    @Override
    public Future<TelemetrySender> getOrCreateTelemetrySender(final String tenantId) {
        try {
            return getAmqpAdapterClientFactory(tenantId).getOrCreateTelemetrySender();
        } catch (Exception ex) {
            return Future.failedFuture(ex);
        }
    }

    @Override
    public Future<EventSender> getOrCreateEventSender(final String tenantId) {
        try {
            return getAmqpAdapterClientFactory(tenantId).getOrCreateEventSender();
        } catch (Exception ex) {
            return Future.failedFuture(ex);
        }
    }

    @Override
    public Future<CommandResponder> getOrCreateCommandResponseSender(final String tenantId) {
        try {
            return getAmqpAdapterClientFactory(tenantId).getOrCreateCommandResponseSender();
        } catch (Exception ex) {
            return Future.failedFuture(ex);
        }
    }

    @Override
    public Future<MessageConsumer> createDeviceSpecificCommandConsumer(final String tenantId, final String deviceId,
            final Consumer<Message> messageHandler) {

        try {
            return getAmqpAdapterClientFactory(tenantId).createDeviceSpecificCommandConsumer(deviceId, messageHandler);
        } catch (Exception ex) {
            return Future.failedFuture(ex);
        }
    }

    @Override
    public Future<MessageConsumer> createCommandConsumer(final String tenantId,
            final Consumer<Message> messageHandler) {

        try {
            return getAmqpAdapterClientFactory(tenantId).createCommandConsumer(messageHandler);
        } catch (Exception ex) {
            return Future.failedFuture(ex);
        }
    }

    private TenantConnections getTenantConnections(final String tenantId) throws IllegalArgumentException {
        final TenantConnections tenantConnections = connectionsPerTenant.get(tenantId);
        if (tenantConnections == null) {
            throw new IllegalArgumentException("tenant [" + tenantId + "] is not connected");
        } else {
            return tenantConnections;
        }
    }

    private AmqpAdapterClientFactory getAmqpAdapterClientFactory(final String tenantId)
            throws IllegalStateException, IllegalArgumentException {
        return getTenantConnections(tenantId).getAmqpAdapterClientFactory();
    }
}
