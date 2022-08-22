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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.hono.client.device.amqp.AmqpAdapterClient;
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
    private AmqpAdapterClient amqpAdapterClient;

    /**
     * Sets up common fixture.
     */
    @BeforeEach
    public void setUp() {
        amqpAdapterClient = mock(AmqpAdapterClient.class);

        tenantConnections = new TenantConnections(amqpAdapterClient, "a-tenant-id");
        endpoint = mock(MqttEndpoint.class);
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

        assertThat(tenantConnections.getAmqpAdapterClient().failed()).isTrue();
    }

    /**
     * Verifies that the instance is NOT closed when an endpoint is closed while other endpoints are still open.
     */
    @Test
    public void instanceIsOpenWhenClosingEndpointThatIsNotTheLastOne() {
        tenantConnections.addEndpoint(endpoint);
        tenantConnections.addEndpoint(mock(MqttEndpoint.class));

        tenantConnections.closeEndpoint(endpoint);

        assertThat(tenantConnections.getAmqpAdapterClient().succeeded()).isTrue();
    }

    /**
     * Verifies that the instance is closed when closeAllConnections() is invoked.
     */
    @Test
    public void instanceIsClosedWhenInvokingClose() {

        tenantConnections.getAmqpAdapterClient();

        tenantConnections.closeAllConnections();

        assertThat(tenantConnections.getAmqpAdapterClient().failed()).isTrue();
    }

    /**
     * Verifies that the isConnected() method delegates the check to the client factory.
     */
    @Test
    public void isConnectedDelegatesToClientFactory() {
        when(amqpAdapterClient.isConnected(anyLong())).thenReturn(Future.succeededFuture());

        tenantConnections.isConnected(5L);
        verify(amqpAdapterClient).isConnected(eq(5L));
    }

    /**
     * Verifies that the connect() method delegates the call to the client factory.
     */
    @Test
    public void connectDelegatesToClientFactory() {
        when(amqpAdapterClient.connect()).thenReturn(Future.succeededFuture());

        tenantConnections.connect();
        verify(amqpAdapterClient).connect();

    }

}
