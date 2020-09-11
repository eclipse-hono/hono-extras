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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.hono.config.ClientConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttEndpoint;

/**
 * Verifies behavior of {@link MultiTenantConnectionManagerImpl}.
 */
public class MultiTenantConnectionManagerImplTest {

    private static final String TENANT_ID = "test-tenant";
    private MultiTenantConnectionManagerImpl connectionManager;
    private MqttEndpoint endpoint;
    private Vertx vertx;

    /**
     * Sets up common fixture.
     */
    @BeforeEach
    public void setUp() {
        connectionManager = new MultiTenantConnectionManagerImpl();
        endpoint = mock(MqttEndpoint.class);

        vertx = mock(Vertx.class);
        when(vertx.getOrCreateContext()).thenReturn(mock(Context.class));
    }

    /**
     * Verifies that closing the last endpoint of the tenant, closes the AMQP connection.
     */
    @Test
    public void amqpConnectionIsClosedWhenClosingLastEndpoint() {

        connectionManager.connect(TENANT_ID, vertx, new ClientConfigProperties());
        connectionManager.addEndpoint(TENANT_ID, endpoint);

        assertThat(connectionManager.closeEndpoint(TENANT_ID, endpoint).result()).isTrue();

    }

    /**
     * Verifies that closing an endpoint while there are others for the same tenant, the AMQP connection is not closed.
     */
    @Test
    public void amqpConnectionIsOpenWhenClosingEndpointThatIsNotTheLastOne() {

        connectionManager.connect(TENANT_ID, vertx, new ClientConfigProperties());
        connectionManager.addEndpoint(TENANT_ID, endpoint);
        connectionManager.addEndpoint(TENANT_ID, mock(MqttEndpoint.class));

        assertThat(connectionManager.closeEndpoint(TENANT_ID, endpoint).result()).isFalse();

    }

    /**
     * Verifies that all tenants are closed when closeAllTenants() is invoked.
     */
    @Test
    public void addEndpointFailsIfInstanceIsClosed() {

        connectionManager.connect(TENANT_ID, vertx, new ClientConfigProperties());

        connectionManager.closeAllTenants();

        assertThat(connectionManager.addEndpoint(TENANT_ID, endpoint).failed()).isTrue();
    }

    /**
     * Verifies that trying to add an endpoint without connecting the tenant first fails.
     */
    @Test
    public void addEndpointFailsIfNotConnected() {
        assertThat(connectionManager.addEndpoint(TENANT_ID, endpoint).failed()).isTrue();
    }

    /**
     * Verifies that trying to close an endpoint without connecting the tenant first fails.
     */
    @Test
    public void closeEndpointFailsIfNotConnected() {
        assertThat(connectionManager.closeEndpoint(TENANT_ID, endpoint).failed()).isTrue();
    }

    /**
     * Verifies that calling one of the methods that delegate to AmqpAdapterClientFactory fails if the tenant is not
     * connected.
     */
    @Test
    public void futureFailsIfNotConnected() {

        assertThat(connectionManager.getOrCreateTelemetrySender(TENANT_ID).failed()).isTrue();

        assertThat(connectionManager.getOrCreateEventSender(TENANT_ID).failed()).isTrue();

        assertThat(connectionManager.getOrCreateCommandResponseSender(TENANT_ID).failed()).isTrue();

        assertThat(connectionManager.createDeviceSpecificCommandConsumer(TENANT_ID, "device-id", msg -> {
        }).failed()).isTrue();

        assertThat(connectionManager.createCommandConsumer(TENANT_ID, msg -> {
        }).failed()).isTrue();

    }

}
