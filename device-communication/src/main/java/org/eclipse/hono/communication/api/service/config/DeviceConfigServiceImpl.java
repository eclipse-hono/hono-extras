/*
 * ***********************************************************
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
 *  <p>
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *  <p>
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *  <p>
 *  SPDX-License-Identifier: EPL-2.0
 * **********************************************************
 *
 */

package org.eclipse.hono.communication.api.service.config;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigAckResponse;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.data.ListDeviceConfigVersionsResponse;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceConfigRepository;
import org.eclipse.hono.communication.api.service.DeviceServiceAbstract;
import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.common.base.Strings;
import com.google.pubsub.v1.PubsubMessage;

import io.vertx.core.Future;


/**
 * Service for device commands.
 */

@Singleton
public class DeviceConfigServiceImpl extends DeviceServiceAbstract implements DeviceConfigService {

    private final Logger log = LoggerFactory.getLogger(DeviceConfigServiceImpl.class);
    private final DeviceConfigRepository repository;
    private final String deviceIdKey = "deviceId";
    private final String tenantIdKey = "tenantId";
    private final String configVersionIdKey = "configVersion";

    private final DeviceConfigMapper mapper;

    /**
     * Creates a new DeviceConfigServiceImpl.
     *
     * @param repository              The device config repository
     * @param mapper                  The device config mapper
     * @param internalMessagingConfig The internal messaging config
     * @param internalMessaging       The internal messaging interface
     */
    public DeviceConfigServiceImpl(final DeviceConfigRepository repository,
                                   final DeviceConfigMapper mapper,
                                   final InternalMessagingConfig internalMessagingConfig,
                                   final InternalMessaging internalMessaging
    ) {

        super(internalMessagingConfig, internalMessaging);

        this.repository = repository;
        this.mapper = mapper;
        subscribeToAllEventTenants();
    }


    /**
     * Subscribe to all tenant event topics.
     */
    void subscribeToAllEventTenants() {
        repository.listTenants()
                .onSuccess(tenants -> tenants
                        .forEach(tenant -> {
                            final var topic = messagingConfig.getOnConnectEventTopicFormat().formatted(tenant);
                            internalMessaging.subscribe(topic, this::onDeviceConnectEvent);

                        })).onFailure(err -> log.error("Error subscribing to all tenant events: {}", err.getMessage()));
    }

    /**
     * Create and publish new device configs.
     *
     * @param deviceConfig The device configs
     * @param deviceId     The device id
     * @param tenantId     The tenant id
     * @return Future of device config
     */
    public Future<DeviceConfig> modifyCloudToDeviceConfig(final DeviceConfigRequest deviceConfig, final String deviceId, final String tenantId) {

        final var entity = mapper.configRequestToDeviceConfigEntity(deviceConfig);
        entity.setDeviceId(deviceId);
        entity.setTenantId(tenantId);

        return repository.createNew(entity)
                .map(mapper::deviceConfigEntityToConfig)
                .onSuccess(result -> {
                    final var topicToPublish = String.format(messagingConfig.getConfigTopicFormat(), entity.getTenantId());
                    final var ackTopicToSubscribe = String.format(messagingConfig.getConfigAckTopicFormat(), entity.getTenantId());
                    final var messageAttributes = Map.of(
                            deviceIdKey, entity.getDeviceId(),
                            tenantId, entity.getTenantId(),
                            configVersionIdKey, result.getVersion());

                    try {
                        final String configJson = ow.writeValueAsString(result);
                        internalMessaging.publish(topicToPublish, configJson, messageAttributes);
                        internalMessaging.subscribe(ackTopicToSubscribe, this::onDeviceConfigAck);

                        log.info("Config {} is published on topic {}", configJson, topicToPublish);
                    } catch (Exception ex) {
                        log.error("Internal communication error: {}", ex.getMessage());
                    }
                });
    }

    /**
     * List all device configs.
     *
     * @param deviceId The device id
     * @param tenantId The tenant id
     * @param limit    The limit max=10
     * @return Future of ListDeviceConfigVersionsResponse
     */
    public Future<ListDeviceConfigVersionsResponse> listAll(final String deviceId, final String tenantId, final int limit) {
        return repository.listAll(deviceId, tenantId, limit)
                .map(
                        result -> {
                            final var listConfig = new ListDeviceConfigVersionsResponse();
                            listConfig.setDeviceConfigs(result);
                            return listConfig;
                        }
                );

    }

    /**
     * Update field deviceAckTime when ack received from the device.
     *
     * @param configAckResponse Device config to ack
     * @param deviceAckTime     Time of ack
     */
    @Override
    public void updateDeviceAckTime(final DeviceConfigAckResponse configAckResponse, final String deviceAckTime) {
        repository.updateDeviceAckTime(configAckResponse, deviceAckTime)
                .onSuccess(ok -> {
                    log.info("Device acknowledged config {}", configAckResponse);
                });
    }

    /**
     * Handle incoming ack config message.
     *
     * @param pubsubMessage The message to handle
     * @param consumer      The message consumer
     */

    public void onDeviceConfigAck(final PubsubMessage pubsubMessage, final AckReplyConsumer consumer) {
        final var messageAttributes = pubsubMessage.getAttributesMap();
        final var deviceId = messageAttributes.get(messagingConfig.getDeviceIdKey());
        final var tenantId = messageAttributes.get(messagingConfig.getTenantIdKey());
        final var version = messageAttributes.get(messagingConfig.getConfigVersionIdKey());

        final var ackResponse = new DeviceConfigAckResponse(version, tenantId, deviceId);

        log.info("New Config ack was received {}", ackResponse);
        updateDeviceAckTime(ackResponse, Instant.now().toString());
        consumer.ack();

    }

    /**
     * Handle incoming device onConnect events.
     *
     * @param pubsubMessage The message to handle
     * @param consumer      The message consumer
     */
    public void onDeviceConnectEvent(final PubsubMessage pubsubMessage, final AckReplyConsumer consumer) {

        consumer.ack(); // message was received and processed only once

        final HashMap payload;
        final String msg = pubsubMessage.getData().toStringUtf8();

        if (Strings.isNullOrEmpty(msg)) {
            log.debug("Skip Event: payload is empty");
            return;
        }

        try {
            payload = or.readValue(msg, HashMap.class);
        } catch (IOException e) {
            log.error("Error deserialize event payload: {}", e.getMessage());
            return;
        }

        final var messageAttributes = pubsubMessage.getAttributesMap();
        final var deviceId = messageAttributes.get(messagingConfig.getDeviceIdKey());
        final var tenantId = messageAttributes.get(messagingConfig.getTenantIdKey());

        if (skipIncomingDeviceEvent(payload, deviceId, tenantId)) {
            return;
        }

        repository.getDeviceLatestConfig(deviceId, tenantId)
                .onSuccess(res -> {
                    final var config = mapper.deviceConfigEntityToConfig(res);
                    final var topicToPublish = String.format(messagingConfig.getConfigTopicFormat(), tenantId);
                    final var ackTopicToSubscribe = String.format(messagingConfig.getConfigAckTopicFormat(), tenantId);
                    try {
                        internalMessaging.publish(topicToPublish, ow.writeValueAsString(config), messageAttributes);
                        internalMessaging.subscribe(ackTopicToSubscribe, this::onDeviceConfigAck);
                        log.info("Handle onConnect event, publish device config {}", res);
                    } catch (Exception ex) {
                        log.error("Error serialize config {}", config);
                    }
                })
                .onFailure(err -> log.error("Can't publish configs: {}", err.getMessage()));
    }

    private boolean skipIncomingDeviceEvent(final HashMap payload, final String deviceId, final String tenantId) {
        if (Strings.isNullOrEmpty(deviceId) || Strings.isNullOrEmpty(tenantId)) {
            log.warn("Skip device onConnect event: deviceId or tenantId is empty");
            return true;
        }

        final var eventType = payload.getOrDefault(messagingConfig.getDeviceConnectPayloadKey(), "");
        if (!eventType.equals(messagingConfig.getDeviceConnectPayloadValue())) {
            log.debug("Skip device event: cause is not equal connected");
            return true;
        }

        return false;
    }


}
