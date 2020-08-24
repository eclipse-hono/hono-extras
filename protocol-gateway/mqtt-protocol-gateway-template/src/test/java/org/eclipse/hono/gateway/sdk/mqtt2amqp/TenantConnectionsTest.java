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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.hono.client.HonoConnection;
import org.eclipse.hono.client.device.amqp.AmqpAdapterClientFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Future;
import io.vertx.mqtt.MqttEndpoint;

/**
 * Verifies behavior of {@link TenantConnections}.
 */
public class TenantConnectionsTest {

    private TenantConnections tenantConnections;
    private MqttEndpoint endpoint;
    private AmqpAdapterClientFactory amqpAdapterClientFactory;

    /**
     * Sets up common fixture.
     */
    @BeforeEach
    public void setUp() {
        amqpAdapterClientFactory = mock(AmqpAdapterClientFactory.class);

        tenantConnections = new TenantConnections(amqpAdapterClientFactory);
        endpoint = mock(MqttEndpoint.class);
    }

    /**
     * Verifies that the connect method returns the instance.
     */
    @Test
    public void connectReturnsTheInstance() {
        when(amqpAdapterClientFactory.connect()).thenReturn(Future.succeededFuture(mock(HonoConnection.class)));

        assertThat(tenantConnections.connect()).isEqualTo(tenantConnections);
    }

    /**
     * Verifies that adding an endpoint works.
     */
    @Test
    public void containsEndpointWhenAdding() {
        tenantConnections.addEndpoint(endpoint);

        assertThat(tenantConnections.mqttEndpoints.size()).isEqualTo(1);
        assertThat(tenantConnections.mqttEndpoints.contains(endpoint)).isTrue();
    }

    /**
     * Verifies that removing an endpoint works.
     */
    @Test
    public void endpointIsRemovedWhenClosingEndpoint() {
        tenantConnections.addEndpoint(endpoint);

        tenantConnections.closeEndpoint(endpoint);

        assertThat(tenantConnections.mqttEndpoints.isEmpty()).isTrue();
    }

    /**
     * Verifies that the instance is closed when the last endpoint is closed.
     */
    @Test
    public void instanceIsClosedWhenClosingLastEndpoint() {
        tenantConnections.addEndpoint(endpoint);

        tenantConnections.closeEndpoint(endpoint);

        Assertions.assertThrows(IllegalStateException.class, () -> tenantConnections.getAmqpAdapterClientFactory());
    }

    /**
     * Verifies that the instance is NOT closed when an endpoint is closed while other endpoints are still open.
     */
    @Test
    public void instanceIsOpenWhenClosingEndpointThatIsNotTheLastOne() {
        tenantConnections.addEndpoint(endpoint);
        tenantConnections.addEndpoint(mock(MqttEndpoint.class));

        tenantConnections.closeEndpoint(endpoint);

        assertThat(tenantConnections.getAmqpAdapterClientFactory()).isNotNull();
    }

    /**
     * Verifies that the instance is closed when closeAllConnections() is invoked.
     */
    @Test
    public void instanceIsClosedWhenInvokingClose() {

        tenantConnections.getAmqpAdapterClientFactory();

        tenantConnections.closeAllConnections();

        Assertions.assertThrows(IllegalStateException.class, () -> tenantConnections.getAmqpAdapterClientFactory());
    }

    /**
     * Verifies that the isConnected() method delegates the check to the client factory.
     */
    @Test
    public void isConnectedDelegatesToClientFactory() {
        tenantConnections.isConnected(5L);
        verify(amqpAdapterClientFactory).isConnected(eq(5L));
    }

}
