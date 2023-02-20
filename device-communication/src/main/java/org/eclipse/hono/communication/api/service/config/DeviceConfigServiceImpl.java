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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;
import io.vertx.core.Future;
import org.eclipse.hono.communication.api.data.DeviceConfigAckResponse;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.data.DeviceConfigResponse;
import org.eclipse.hono.communication.api.data.ListDeviceConfigVersionsResponse;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceConfigsRepository;
import org.eclipse.hono.communication.api.service.command.DeviceCommandServiceImpl;
import org.eclipse.hono.communication.api.service.communication.InternalCommunication;
import org.eclipse.hono.communication.api.service.database.DatabaseService;
import org.eclipse.hono.communication.core.app.InternalCommunicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.time.Instant;


/**
 * Service for device commands
 */

@Singleton
public class DeviceConfigServiceImpl implements DeviceConfigService, MessageReceiver {
    private final static ObjectWriter ow = new ObjectMapper().writer();
    private final static ObjectReader or = new ObjectMapper().reader();
    private final Logger log = LoggerFactory.getLogger(DeviceCommandServiceImpl.class);
    private final DeviceConfigsRepository repository;
    private final DatabaseService db;
    private final DeviceConfigMapper mapper;
    private final InternalCommunicationConfig communicationConfig;


    private final InternalCommunication internalCommunication;

    public DeviceConfigServiceImpl(DeviceConfigsRepository repository,
                                   DatabaseService db,
                                   DeviceConfigMapper mapper,
                                   InternalCommunicationConfig communicationConfig,
                                   InternalCommunication internalCommunication) {

        this.repository = repository;
        this.db = db;
        this.mapper = mapper;
        this.communicationConfig = communicationConfig;
        this.internalCommunication = internalCommunication;
    }


    public Future<DeviceConfigResponse> modifyCloudToDeviceConfig(DeviceConfigRequest deviceConfig, String deviceId, String tenantId) {

        var entity = mapper.configRequestToDeviceConfigEntity(deviceConfig);
        entity.setDeviceId(deviceId);
        entity.setTenantId(tenantId);

        return db.getDbClient().withTransaction(
                        sqlConnection ->
                                repository.createNew(sqlConnection, entity))
                .map(mapper::deviceConfigEntityToResponse)
                .onSuccess(result -> {
                    var topicToPublish = String.format(communicationConfig.getConfigTopicFormat(), entity.getTenantId());
                    var ackTopicToSubscribe = String.format(communicationConfig.getConfigAckTopicFormat(), entity.getTenantId());


                    try {
                        String configJson = ow.writeValueAsString(result);
                        internalCommunication.publish(topicToPublish, configJson);
                        internalCommunication.subscribe(ackTopicToSubscribe, this);

                        log.info("Config {} is published on topic {}", configJson, topicToPublish);
                    } catch (Exception ex) {
                        log.error("Internal communication error: {}", ex.getMessage());
                    }
                });
    }

    public Future<ListDeviceConfigVersionsResponse> listAll(String deviceId, String tenantId, int limit) {
        return db.getDbClient().withConnection(
                sqlConnection -> repository.listAll(sqlConnection, deviceId, tenantId, limit)
                        .map(
                                result -> {
                                    var listConfig = new ListDeviceConfigVersionsResponse();
                                    listConfig.setDeviceConfigs(result);
                                    return listConfig;
                                }
                        )
        );

    }

    /**
     * Update field deviceAckTime when ack received from the device
     *
     * @param configAckResponse Device config to ack
     * @param deviceAckTime     Time of ack
     * @return Future of Void
     */
    @Override
    public void updateDeviceAckTime(DeviceConfigAckResponse configAckResponse, String deviceAckTime) {
        db.getDbClient().withTransaction(
                sqlConnection -> {
                    return repository.updateDeviceAckTime(sqlConnection, configAckResponse, deviceAckTime);
                }
        ).onSuccess(ok -> {
            log.info("Device acknowledged config {}", configAckResponse);
        });
    }

    /**
     * Handle incoming topic message
     *
     * @param msg      The message to handle
     * @param consumer The message consumer
     */
    @Override
    public void receiveMessage(PubsubMessage msg, AckReplyConsumer consumer) {
        try {
            var ackResponse = or.readValue(msg.getData().toStringUtf8(), DeviceConfigAckResponse.class);
            log.info("New Config ack was received: {}", ackResponse.toString());
            updateDeviceAckTime(ackResponse, Instant.now().toString());
            consumer.ack();
        } catch (IOException ex) {
            log.error("Device config ack response can't deserialization error: {}", ex.getMessage());
        }

    }


}
