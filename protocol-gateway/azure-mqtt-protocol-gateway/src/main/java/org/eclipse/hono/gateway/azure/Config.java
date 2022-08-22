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

package org.eclipse.hono.gateway.azure;

import java.util.List;

import org.eclipse.hono.client.amqp.config.ClientConfigProperties;
import org.eclipse.hono.gateway.sdk.mqtt2amqp.MqttProtocolGatewayConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot configuration for the the "Azure IoT Hub" Protocol Gateway.
 */
@Configuration
public class Config {

    /**
     * These are the default secure protocols in Vertx.
     */
    public static final List<String> enabledProtocols = List.of("TLSv1", "TLSv1.1", "TLSv1.2");

    /**
     * Exposes configuration properties for providing an MQTT server as a Spring bean.
     * <p>
     * Sets the TLS protocols from {@link #enabledProtocols} as the enabled secure protocols of the MQTT server if not
     * set explicitly.
     *
     * @return The properties.
     */
    @Bean
    @ConfigurationProperties(prefix = "hono.server.mqtt")
    public MqttProtocolGatewayConfig mqttGatewayConfig() {
        final MqttProtocolGatewayConfig mqttProtocolGatewayConfig = new MqttProtocolGatewayConfig();
        mqttProtocolGatewayConfig.setSecureProtocols(enabledProtocols);
        return mqttProtocolGatewayConfig;
    }

    /**
     * Exposes configuration properties for accessing Hono's AMQP adapter as a Spring bean.
     *
     * @return The properties.
     */
    @Bean
    @ConfigurationProperties(prefix = "hono.client.amqp")
    public ClientConfigProperties amqpClientConfig() {
        final ClientConfigProperties amqpClientConfig = new ClientConfigProperties();
        amqpClientConfig.setServerRole("AMQP Adapter");
        return amqpClientConfig;
    }

    /**
     * Creates a new Azure IoT Hub protocol gateway instance.
     *
     * @return The new instance.
     * @throws IllegalArgumentException If the configuration is invalid.
     */
    @Bean
    public AzureIotHubMqttGateway azureIotHubMqttGateway() {
        final DemoDeviceConfiguration demoDeviceConfig = demoDevice();
        final ClientConfigProperties amqpClientConfig = amqpClientConfig();

        if (demoDeviceConfig.getTenantId() == null || demoDeviceConfig.getDeviceId() == null) {
            throw new IllegalArgumentException("Demo device is not configured.");
        }
        if (amqpClientConfig.getUsername() == null || amqpClientConfig.getPassword() == null) {
            throw new IllegalArgumentException("Gateway credentials are not configured.");
        }
        if (amqpClientConfig.getHost() == null) {
            throw new IllegalArgumentException("AMQP host is not configured.");
        }

        return new AzureIotHubMqttGateway(amqpClientConfig, mqttGatewayConfig(), demoDeviceConfig);
    }

    /**
     * Exposes configuration properties for a demo device as a Spring bean.
     *
     * @return The demo device configuration against which the authentication of a connecting device is being performed.
     */
    @Bean
    @ConfigurationProperties(prefix = "hono.demo.device")
    public DemoDeviceConfiguration demoDevice() {
        return new DemoDeviceConfiguration();
    }

}
