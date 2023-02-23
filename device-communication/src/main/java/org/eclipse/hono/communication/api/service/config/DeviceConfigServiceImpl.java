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


import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.common.base.Strings;
import com.google.pubsub.v1.PubsubMessage;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigAckResponse;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.data.ListDeviceConfigVersionsResponse;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceConfigRepository;
import org.eclipse.hono.communication.api.service.DeviceServiceAbstract;
import org.eclipse.hono.communication.api.service.command.DeviceCommandServiceImpl;
import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


/**
 * Service for device commands
 */

@Singleton
public class DeviceConfigServiceImpl extends DeviceServiceAbstract implements DeviceConfigService {

    private final Logger log = LoggerFactory.getLogger(DeviceCommandServiceImpl.class);
    private final DeviceConfigRepository repository;
    private final String deviceIdKey = "deviceId";
    private final String tenantIdKey = "tenantId";
    private final String configVersionIdKey = "configVersion";

    private final DeviceConfigMapper mapper;


    private final InternalMessaging internalMessaging;

    public DeviceConfigServiceImpl(DeviceConfigRepository repository,
                                   DeviceConfigMapper mapper,
                                   InternalMessagingConfig communicationConfig,
                                   InternalMessaging internalMessaging
    ) {

        super(communicationConfig, internalMessaging);

        this.repository = repository;
        this.mapper = mapper;
        this.internalMessaging = internalMessaging;
        subscribeToAllEventTenants();
    }


    /**
     * Subscribe to all tenant event topics
     */
    void subscribeToAllEventTenants() {
        repository.listTenants()
                .onSuccess(tenants -> tenants
                        .forEach(tenant -> {
                            var topic = messagingConfig.getOnConnectEventTopicFormat().formatted(tenant);
                            internalMessaging.subscribe(topic, this::onDeviceConnectEvent);

                        })).onFailure(err -> log.error("Error subscribing to all tenant events: {}", err.getMessage()));
        final JsonObject payload = new JsonObject();
        payload.put("cause", "test");
        log.info("{}", payload.toBuffer());
    }

    /**
     * Create and publish new device configs
     *
     * @param deviceConfig The device configs
     * @param deviceId     The device id
     * @param tenantId     The tenant id
     * @return Future of device config
     */
    public Future<DeviceConfig> modifyCloudToDeviceConfig(DeviceConfigRequest deviceConfig, String deviceId, String tenantId) {

        var entity = mapper.configRequestToDeviceConfigEntity(deviceConfig);
        entity.setDeviceId(deviceId);
        entity.setTenantId(tenantId);

        return repository.createNew(entity)
                .map(mapper::deviceConfigEntityToDeviceConfig)
                .onSuccess(result -> {
                    var topicToPublish = String.format(messagingConfig.getConfigTopicFormat(), entity.getTenantId());
                    var ackTopicToSubscribe = String.format(messagingConfig.getConfigAckTopicFormat(), entity.getTenantId());
                    var messageAttributes = Map.of(
                            deviceIdKey, entity.getDeviceId(),
                            tenantId, entity.getTenantId(),
                            configVersionIdKey, result.getVersion());

                    try {
                        String configJson = ow.writeValueAsString(result);
                        internalMessaging.publish(topicToPublish, configJson, messageAttributes);
                        internalMessaging.subscribe(ackTopicToSubscribe, this::onDeviceConfigAck);

                        log.info("Config {} is published on topic {}", configJson, topicToPublish);
                    } catch (Exception ex) {
                        log.error("Internal communication error: {}", ex.getMessage());
                    }
                });
    }

    /**
     * List all device configs
     *
     * @param deviceId The device id
     * @param tenantId The tenant id
     * @param limit    The limit max=10
     * @return Future of ListDeviceConfigVersionsResponse
     */
    public Future<ListDeviceConfigVersionsResponse> listAll(String deviceId, String tenantId, int limit) {
        return repository.listAll(deviceId, tenantId, limit)
                .map(
                        result -> {
                            var listConfig = new ListDeviceConfigVersionsResponse();
                            listConfig.setDeviceConfigs(result);
                            return listConfig;
                        }
                );

    }

    /**
     * Update field deviceAckTime when ack received from the device
     *
     * @param configAckResponse Device config to ack
     * @param deviceAckTime     Time of ack
     */
    @Override
    public void updateDeviceAckTime(DeviceConfigAckResponse configAckResponse, String deviceAckTime) {
        repository.updateDeviceAckTime(configAckResponse, deviceAckTime)
                .onSuccess(ok -> {
                    log.info("Device acknowledged config {}", configAckResponse);
                });
    }

    /**
     * Handle incoming ack config message
     *
     * @param pubsubMessage The message to handle
     * @param consumer      The message consumer
     */

    public void onDeviceConfigAck(PubsubMessage pubsubMessage, AckReplyConsumer consumer) {
        var messageAttributes = pubsubMessage.getAttributesMap();
        var deviceId = messageAttributes.get(messagingConfig.getDeviceIdKey());
        var tenantId = messageAttributes.get(messagingConfig.getTenantIdKey());
        var version = messageAttributes.get(messagingConfig.getConfigVersionIdKey());

        var ackResponse = new DeviceConfigAckResponse(version, tenantId, deviceId);

        log.info("New Config ack was received {}", ackResponse);
        updateDeviceAckTime(ackResponse, Instant.now().toString());
        consumer.ack();

    }

    /**
     * Handle incoming device onConnect events
     *
     * @param pubsubMessage The message to handle
     * @param consumer      The message consumer
     */
    public void onDeviceConnectEvent(PubsubMessage pubsubMessage, AckReplyConsumer consumer) {

        consumer.ack(); // message was received and processed only once

        HashMap payload;
        String msg = pubsubMessage.getData().toStringUtf8();

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

        var messageAttributes = pubsubMessage.getAttributesMap();
        var deviceId = messageAttributes.get(messagingConfig.getDeviceIdKey());
        var tenantId = messageAttributes.get(messagingConfig.getTenantIdKey());

        if (skipIncomingDeviceEvent(payload, deviceId, tenantId)) {
            return;
        }

        repository.getDeviceLatestConfig(deviceId, tenantId)
                .onSuccess(res -> {
                    var config = mapper.deviceConfigEntityToDeviceConfig(res);
                    var topicToPublish = String.format(messagingConfig.getConfigTopicFormat(), tenantId);
                    var ackTopicToSubscribe = String.format(messagingConfig.getConfigAckTopicFormat(), tenantId);
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

    private boolean skipIncomingDeviceEvent(HashMap payload, String deviceId, String tenantId) {
        if (Strings.isNullOrEmpty(deviceId) || Strings.isNullOrEmpty(tenantId)) {
            log.warn("Skip device onConnect event: deviceId or tenantId is empty");
            return true;
        }

        var eventType = payload.getOrDefault(messagingConfig.getDeviceConnectPayloadKey(), "");
        if (!eventType.equals(messagingConfig.getDeviceConnectPayloadValue())) {
            log.debug("Skip device event: cause is not equal connected");
            return true;
        }

        return false;
    }


}
